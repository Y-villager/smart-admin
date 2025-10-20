package net.lab1024.sa.admin.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

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
     * @param entityList 数据列表
     * @param dao   dao层 Class类
     * @param methodName  方法名
     */
    public int doThreadInsertOrUpdate(List<?> entityList, Object dao, String methodName) {
        AtomicInteger successTotal = new AtomicInteger(0);

        // 初始化线程池
        ThreadPoolExecutor threadPool = ThreadPoolUtils.createThreadPool();

        // 将大集合拆分成N个小集合，使用多线程操作数据
        List<List<?>> splitList = createBatches(entityList);

        // 记录单个任务的执行次数
        CountDownLatch countDownLatch = new CountDownLatch(splitList.size());
        for (List<?> subList : splitList) {
            threadPool.execute(new Thread(() -> {
                // 为每个线程创建独立的事务
                DefaultTransactionDefinition def = new DefaultTransactionDefinition();
                def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
                TransactionStatus status = transactionManager.getTransaction(def);

                try {
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
}