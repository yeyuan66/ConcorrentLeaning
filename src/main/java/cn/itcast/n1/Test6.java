package cn.itcast.n1;

import lombok.extern.slf4j.Slf4j;

@Slf4j(topic = "c.Test6")
public class Test6 {
    public static void main(String[] args) {
        Thread t6 = new Thread("t6") {
            @Override
            public void run() {
                log.debug("enter sleep");
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        t6.start();
        log.debug("t6 state:{}",t6.getState());
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        log.debug("t6 state:{}",t6.getState());
    }
}
