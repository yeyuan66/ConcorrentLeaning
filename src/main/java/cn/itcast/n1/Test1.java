package cn.itcast.n1;

import lombok.extern.slf4j.Slf4j;

@Slf4j(topic = "c.test1")
public class Test1 {
    public static void main(String[] args) {
        Thread t = new Thread(() -> log.debug("hello,t"));
        Thread t1 = new Thread("t1") {
            @Override
// run 方法内实现了要执行的任务
            public void run() {
                log.debug("hello");

            }
        };
        t.start();
        t1.start();
    }
}
