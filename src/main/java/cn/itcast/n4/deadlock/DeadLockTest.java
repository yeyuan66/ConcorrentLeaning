package cn.itcast.n4.deadlock;

import lombok.extern.slf4j.Slf4j;

@Slf4j(topic = "c.DeadLockTest")
public class DeadLockTest {
    private  static final Object Lock_A = new Object();
    private  static final Object Lock_B = new Object();
    public static void main(String[] args) {
        Thread t1 = new Thread(() -> {
            //先获取第一把锁，保证t2获取B锁时，再去获取B锁
            synchronized (Lock_A){
                log.info("获取A锁");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                synchronized (Lock_B){
                    log.info("A操作");
                }

            }
        }, "t1");
        t1.start();

        Thread t2 = new Thread(() -> {
            //先获取第一把锁，保证t2获取B锁时，再去获取B锁
            synchronized (Lock_B){
                log.info("获取B锁");
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                synchronized (Lock_A){
                    log.info("B操作");
                }
            }
        }, "t2");
        t2.start();
    }
}
