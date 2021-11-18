package cn.itcast.n4;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.locks.LockSupport;

@Slf4j(topic = "c.ParkTest02")
public class ParkTest02 {
    public static void main(String[] args) {
        //测试先unPark再Park
        Thread t1 = new Thread(() -> {
            log.info("begin");
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            log.info("park");
            LockSupport.park();
            log.info("resume");
        }, "t1");
        t1.start();

        log.info("unPark");
        LockSupport.unpark(t1);
    }
}
