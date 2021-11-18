package cn.itcast.n1;

import lombok.extern.slf4j.Slf4j;

@Slf4j(topic = "c.Test2")
public class Test2 {
    public static void main(String[] args) {
        Runnable task2 = new Runnable() {
            @Override
            public void run() {
                log.debug("hello");
            }
        };
        Thread t2 = new Thread(task2,"t2");
        t2.start();

    }
}
