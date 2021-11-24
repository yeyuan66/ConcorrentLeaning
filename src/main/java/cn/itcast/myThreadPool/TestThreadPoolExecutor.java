package cn.itcast.myThreadPool;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j(topic = "c.TestThreadPoolExecutor")
public class TestThreadPoolExecutor {
    public static void main(String[] args) throws InterruptedException {

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


        //2.CachedThreadPool
        //没有核心线程，最大线程数为Integer.MAX_VALUE
        //救急线程无限创建，且生存时间都是60s
        //阻塞队列采用Synchronous进行创建，特点是没有边界，在创建时必须有人消费才能创建，否则就必须阻塞等待，也即一一配对
        //适用场景：任务数量多，执行时间较短
        ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
        cachedThreadPool.execute(() -> log.debug("1"));

        //测试同步队列的使用
        testSynchronousQueue();

        //3.newSingleThreadExecutor
        //只有一个线程，再有任务放进无边界队列
        //当前任务执行失败会重新创建新的线程进行。
        ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();


    }

    //测试同步队列SynchronousQueue
    public static void testSynchronousQueue() throws InterruptedException {
        SynchronousQueue<Integer> integers = new SynchronousQueue<>();

        //开启一个线程进行元素的添加
        new Thread(() -> {
            for (int i = 0; i < 3; i++) {
                log.debug("尝试添加元素");
                try {
                    integers.put(i);

                    //put会抛出打断异常，可见，添加元素是会被阻塞的
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                log.debug("添加元素完成");
            }
        }).start();

        Thread.sleep(1000);
        //开启一个线程进行元素的获取
        new Thread(() -> {
            for (int i = 0; i < 3; i++) {
                log.debug("尝试获取元素");
                Integer poll = integers.poll();
                log.debug("获取元素成功：{}",poll);
                log.debug("获取元素完成");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    //测试发现，添加线程开始后一秒才开始获取线程，但在这之前元素并没有添加进去，而且添加和获取是一一对应的。
}
