package cn.itcast.JUCLeaning.aqs.countDownLatch;


import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j(topic = "c.CountDownLatchTest1")
public class CountDownLatchTest2 {

    //创建大小为2的countDownLatch
    private static volatile CountDownLatch countDownLatch = new CountDownLatch(2);

    public static void main(String[] args) throws InterruptedException {

        //实际 一般使用线程池，因为线程池对象不能join，使用cl更方便
        //cl可以在任何时候让方法返回，但不用
        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(2);


        fixedThreadPool.submit(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }finally {
                countDownLatch.countDown();

            }
            log.debug("线程一完毕");
        },"t1");

        fixedThreadPool.submit(() -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }finally {
                countDownLatch.countDown();

            }
            log.debug("线程二完毕");
        },"t2");

        log.debug("wait for all");
        countDownLatch.await();
        log.debug("finished");
        fixedThreadPool.shutdown();
    }
}
