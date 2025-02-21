package net.lab1024.sa.admin.util;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPoolUtils {

    /**
     * 创建并返回一个自定义配置的线程池
     *
     * @return 自定义线程池
     */
    public static ThreadPoolExecutor createThreadPool() {
        // 获取可用的 CPU 核心数
        int availableProcessors = Runtime.getRuntime().availableProcessors();
        // 最大线程数设置为核心数的两倍
        int maxPoolSize = availableProcessors * 2;

        // 初始化线程池
        return new ThreadPoolExecutor(
                availableProcessors,      // 核心线程数
                maxPoolSize,       // 最大线程数
                4, TimeUnit.SECONDS, // 线程空闲时最大存活时间
                new ArrayBlockingQueue<>(10), // 队列大小
                new ThreadPoolExecutor.CallerRunsPolicy() // 饱和策略
        );
    }
}
