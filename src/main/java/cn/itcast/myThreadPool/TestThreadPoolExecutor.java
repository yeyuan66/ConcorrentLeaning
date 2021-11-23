package cn.itcast.myThreadPool;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j(topic = "c.TestThreadPoolExecutor")
public class TestThreadPoolExecutor {
    public static void main(String[] args) {

        //Executors通过工厂方法创建的几种线程池
        //1. newFixedThreadPool
        //核心线程数等于最大线程数，阻塞队列无边界，没有拒绝策略
        //可自定义线程工厂，作用是可以给线程起名字
        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(2, new ThreadFactory() {
            private AtomicInteger atomicInteger = new AtomicInteger(1);
            @Override
            public Thread newThread(Runnable r) {

                return new Thread(r,"myPool_t"+atomicInteger.getAndIncrement());
            }
        });
        fixedThreadPool.execute(() -> log.debug("1"));

        fixedThreadPool.execute(() -> log.debug("2"));

        fixedThreadPool.execute(() -> log.debug("3"));
        //核心线程执行完毕后并不立即结束





    }
}
