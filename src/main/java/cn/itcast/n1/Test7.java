package cn.itcast.n1;

import lombok.extern.slf4j.Slf4j;

@Slf4j(topic = "c.Test7")
public class Test7 {
    public static void main(String[] args) {
        Thread t7 = new Thread("t7") {
            @Override
            public void run() {
                log.debug("enter sleep:");
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    log.debug("wake up");
                    e.printStackTrace();
                }
            }
        };
        t7.start();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        log.debug("interrupting t7");
        t7.interrupt();


    }
}
