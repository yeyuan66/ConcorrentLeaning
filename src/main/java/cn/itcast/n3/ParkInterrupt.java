package cn.itcast.n3;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.locks.LockSupport;

@Slf4j(topic = "c.ParkInterrupt")
public class ParkInterrupt {
    public static void main(String[] args) throws InterruptedException {
        Thread t1 = new Thread(()->{
            log.info("park");
            LockSupport.park();
            log.info("un_park");
            log.info("标记：{}",Thread.currentThread().isInterrupted());
            Thread.interrupted();//调用Thread的interrupted会清空打断标记
            log.info("标记：{}",Thread.currentThread().isInterrupted());
        },"t1");
        t1.start();

        Thread.sleep(1000);
        t1.interrupt();



    }
}
