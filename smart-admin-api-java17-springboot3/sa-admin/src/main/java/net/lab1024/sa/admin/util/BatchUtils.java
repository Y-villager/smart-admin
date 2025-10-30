package net.lab1024.sa.admin.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static cn.dev33.satoken.SaManager.log;

@Component
public class BatchUtils {
    private static final Integer batchSize = 1000;

    @Autowired
    private PlatformTransactionManager transactionManager;

    // 将列表分割成多个批次
    private List<List<?>> createBatches(List<?> list) {
        List<List<?>> batches = new ArrayList<>();
        int totalSize = list.size();
        for (int i = 0; i < totalSize; i += batchSize) {
            int end = Math.min(i + batchSize, totalSize);
            batches.add(list.subList(i, end));
        }
        return batches;
    }


    /**
     * 批量更新
     *
     * @param entityList 数据列表
     * @param dao        dao层 Class类
     * @param methodName 方法名
     */
    public int doThreadInsertOrUpdate(List<?> entityList,
                                      Object dao,
                                      String methodName,
                                      Boolean useMainTransaction) {
        AtomicInteger successTotal = new AtomicInteger(0);

        // 初始化线程池
        ThreadPoolExecutor threadPool = ThreadPoolUtils.createThreadPool();

        // 将大集合拆分成N个小集合，使用多线程操作数据
        List<List<?>> splitList = createBatches(entityList);

        // 记录单个任务的执行次数
        CountDownLatch countDownLatch = new CountDownLatch(splitList.size());
        for (List<?> subList : splitList) {
            threadPool.execute(new Thread(() -> {
                TransactionStatus status = null;
                try {
                    if (useMainTransaction) {
                        // 参与外层事务
                        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
                        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
                        status = transactionManager.getTransaction(def);
                    } else {
                        // 独立事务
                        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
                        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
                        status = transactionManager.getTransaction(def);
                    }

                    // 使用反射调用 batchUpdate 方法
                    Method batchUpdateMethod = dao.getClass().getMethod(methodName, List.class);
                    Object result = batchUpdateMethod.invoke(dao, subList);

                    if (result instanceof Integer) {
                        successTotal.addAndGet((Integer) result * subList.size()); // Add the count of successful updates to the total
                    }
                    // 提交事务
                    transactionManager.commit(status);
                } catch (Exception e) {
                    e.printStackTrace();
                    // 回滚事务
                    transactionManager.rollback(status);
                } finally {
                    countDownLatch.countDown();
                }
            }));
        }
        try {
            // 让当前线程处于阻塞状态，知道锁存器计数为零
            countDownLatch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return successTotal.get();
    }

    public int doThreadInsertOrUpdate(List<?> entityList,
                                      Object dao,
                                      String methodName) {
        AtomicInteger successTotal = new AtomicInteger(0);

        // 根据数据量动态调整线程池大小和批次大小
        int optimalThreadPoolSize = calculateOptimalThreadPoolSize(entityList.size());
        int optimalBatchSize = calculateOptimalBatchSize(entityList.size());

        // 创建的线程池
        ThreadPoolExecutor threadPool = ThreadPoolUtils.createThreadPool();

        // 根据优化后的批次大小重新拆分数据
        List<List<?>> splitList = createBatches(entityList, optimalBatchSize);

        // 记录单个任务的执行次数
        CountDownLatch countDownLatch = new CountDownLatch(splitList.size());

        // 使用信号量控制并发度，限制同时执行的数据库事务数量
        Semaphore semaphore = new Semaphore(Math.min(3, optimalThreadPoolSize)); // 进一步降低并发度

        // 添加重试机制和顺序延迟
        AtomicInteger batchCounter = new AtomicInteger(0);
        List<Exception> fatalErrors = Collections.synchronizedList(new ArrayList<>());

        for (int i = 0; i < splitList.size(); i++) {
            final List<?> subList = splitList.get(i);
            final int batchIndex = i;

            threadPool.execute(() -> {
                TransactionStatus status = null;
                int retryCount = 0;
                final int maxRetries = 2; // 减少重试次数

                while (retryCount <= maxRetries) {
                    try {
                        // 获取信号量许可，控制并发
                        semaphore.acquire();

                        // 添加顺序延迟，避免所有事务同时开始
                        if (batchIndex > 0) {
                            try {
                                Thread.sleep(batchIndex * 20L); // 增加延迟时间
                            } catch (InterruptedException ie) {
                                Thread.currentThread().interrupt();
                                break;
                            }
                        }

                        // 设置事务超时时间
                        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
                        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
                        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED); // 降低隔离级别
                        def.setTimeout(30); // 设置30秒超时
                        status = transactionManager.getTransaction(def);

                        // 使用反射调用方法
                        Method batchUpdateMethod = dao.getClass().getMethod(methodName, List.class);
                        Object result = batchUpdateMethod.invoke(dao, subList);

                        if (result instanceof Integer) {
                            // 修正：这里应该是直接加结果，而不是乘以子列表大小
                            successTotal.addAndGet((Integer) result);
                        }

                        // 提交事务
                        transactionManager.commit(status);
                        break; // 成功则跳出重试循环

                    } catch (Exception e) {
                        retryCount++;

                        // 回滚当前事务
                        if (status != null && !status.isCompleted()) {
                            try {
                                transactionManager.rollback(status);
                            } catch (Exception rollbackEx) {
                                // 记录回滚异常但不影响重试
                            }
                        }

                        if (retryCount > maxRetries) {
                            // 达到最大重试次数，记录错误
                            String errorMsg = String.format("批次 %d 插入失败，已达最大重试次数: %s",
                                    batchIndex, e.getMessage());
                            log.error(errorMsg);
                            fatalErrors.add(new RuntimeException(errorMsg, e));
                        } else {
                            // 重试前等待随机时间
                            try {
                                Thread.sleep(ThreadLocalRandom.current().nextInt(100, 500));
                            } catch (InterruptedException ie) {
                                Thread.currentThread().interrupt();
                                break;
                            }
                            log.warn("批次 {} 第 {} 次重试", batchIndex, retryCount);
                        }
                    } finally {
                        semaphore.release();

                        // 只有最终完成（成功或达到最大重试）时才计数
                        if (retryCount > maxRetries || (status != null && status.isCompleted())) {
                            countDownLatch.countDown();

                            // 记录进度
                            int completed = batchCounter.incrementAndGet();
                            if (completed % 10 == 0) {
                                log.info("批量插入进度: {}/{}", completed, splitList.size());
                            }
                        }
                    }
                }
            });
        }

        try {
            // 设置总体超时时间，避免无限等待
            boolean completed = countDownLatch.await(10, TimeUnit.MINUTES);
            if (!completed) {
                throw new RuntimeException("批量插入操作超时");
            }

            // 检查是否有致命错误
            if (!fatalErrors.isEmpty()) {
                throw new RuntimeException("批量插入过程中发生错误", fatalErrors.get(0));
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("批量插入操作被中断", e);
        } finally {
            // 关闭线程池
            threadPool.shutdown();
            try {
                if (!threadPool.awaitTermination(60, TimeUnit.SECONDS)) {
                    threadPool.shutdownNow();
                }
            } catch (InterruptedException e) {
                threadPool.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }

        log.info("批量插入完成: 成功处理 {} 条记录", successTotal.get());
        return successTotal.get();
    }

    /**
     * 根据总数据量计算最优线程池大小
     */
    private int calculateOptimalThreadPoolSize(int totalSize) {
        if (totalSize > 50000) return 3;      // 5万条以上：3个线程
        else if (totalSize > 30000) return 4;  // 3-5万条：4个线程
        else if (totalSize > 20000) return 5;  // 2-3万条：5个线程
        else if (totalSize > 10000) return 6;  // 1-2万条：6个线程
        else if (totalSize > 5000) return 8;   // 5千-1万条：8个线程
        else return 10;                        // 5千条以下：10个线程
    }

    /**
     * 根据总数据量计算最优批次大小
     */
    private int calculateOptimalBatchSize(int totalSize) {
        if (totalSize > 50000) return 100;    // 5万条以上：每批100条
        else if (totalSize > 30000) return 200; // 3-5万条：每批200条
        else if (totalSize > 20000) return 300; // 2-3万条：每批300条
        else if (totalSize > 10000) return 500; // 1-2万条：每批500条
        else return 1000;                      // 1万条以下：每批1000条
    }

    /**
     * 创建批次（使用优化后的批次大小）
     */
    private List<List<?>> createBatches(List<?> entityList, int batchSize) {
        List<List<?>> batches = new ArrayList<>();
        for (int i = 0; i < entityList.size(); i += batchSize) {
            int end = Math.min(entityList.size(), i + batchSize);
            batches.add(new ArrayList<>(entityList.subList(i, end)));
        }
        log.info("数据拆分完成: 总记录数={}, 批次大小={}, 总批次数={}",
                entityList.size(), batchSize, batches.size());
        return batches;
    }
}