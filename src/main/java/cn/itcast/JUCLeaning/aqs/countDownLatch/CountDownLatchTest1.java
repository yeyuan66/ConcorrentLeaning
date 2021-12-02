package cn.itcast.JUCLeaning.aqs.countDownLatch;


import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CountDownLatch;

@Slf4j(topic = "c.CountDownLatchTest1")
public class CountDownLatchTest1 {

    //创建大小为2的countDownLatch
    private static volatile CountDownLatch countDownLatch = new CountDownLatch(2);

    public static void main(String[] args) throws InterruptedException {


        new Thread(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }finally {
                countDownLatch.countDown();

            }
            log.debug("线程一完毕");
        },"t1").start();

        new Thread(() -> {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }finally {
                countDownLatch.countDown();
            }
            log.debug("线程二完毕");

        },"t2").start();

        log.debug("wait for all");
        countDownLatch.await();
        log.debug("finished");
    }
}
