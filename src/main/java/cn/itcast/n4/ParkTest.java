package cn.itcast.n4;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.locks.LockSupport;

@Slf4j(topic = "c.ParkTest")
public class ParkTest {
    public static void main(String[] args) {
        //测试先Park再 unPark
        Thread t1 = new Thread(() -> {
            log.info("begin");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            LockSupport.park();
            log.info("resume");
        }, "t1");

        t1.start();

        Thread t2 = new Thread(() -> {
            log.info("unPark");
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            LockSupport.unpark(t1);
        }, "t2");
        t2.start();


    }
}
