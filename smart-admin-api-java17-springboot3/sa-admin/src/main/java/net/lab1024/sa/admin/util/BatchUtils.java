package net.lab1024.sa.admin.util;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class BatchUtils {
    private static final Integer batchSize = 1000;

    // 将列表分割成多个批次
    private static List<List<?>> createBatches(List<?> list) {
        List<List<?>> batches = new ArrayList<>();
        int totalSize = list.size();
        for (int i = 0; i < totalSize; i += batchSize) {
            int end = Math.min(i + batchSize, totalSize);
            batches.add(list.subList(i, end));
        }
        return batches;
    }

    public static int doThreadUpdate(List<?> entityList, Object dao) {
        AtomicInteger successTotal = new AtomicInteger(0);

        // 初始化线程池
        ThreadPoolExecutor threadPool = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(), 50,
                4, TimeUnit.SECONDS, new ArrayBlockingQueue<>(10),
                new ThreadPoolExecutor.CallerRunsPolicy());

        // 将大集合拆分成N个小集合，使用多线程操作数据
        List<List<?>> splitList = createBatches(entityList);

        // 记录单个任务的执行次数
        CountDownLatch countDownLatch = new CountDownLatch(splitList.size());
        for (List<?> subList : splitList) {
            threadPool.execute(new Thread(() -> {
                try {
                    // 使用反射调用 batchUpdate 方法
                    Method batchUpdateMethod = dao.getClass().getMethod("batchUpdate", List.class);
                    Object result = batchUpdateMethod.invoke(dao, subList);
                    if (result instanceof Integer) {
                        successTotal.addAndGet((Integer) result * subList.size()); // Add the count of successful updates to the total
                    }
                } catch (Exception e) {
                    e.printStackTrace();
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