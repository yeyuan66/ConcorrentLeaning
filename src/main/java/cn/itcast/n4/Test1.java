package cn.itcast.n4;

import lombok.extern.slf4j.Slf4j;

@Slf4j(topic = "c.Test1")
public class Test1 {
    static int x =0;
    static final Object lock = new Object();
    public static void main(String[] args) throws InterruptedException {

        Thread t1 = new Thread(() ->{

            synchronized (lock){
                for (int i = 0; i < 5000; i++) {
                    x++;
                }
            }
        },"t1");
        Thread t2 = new Thread(() -> {

            synchronized (lock){
                for (int i = 0; i < 5000; i++) {
                    x--;
                }
            }

        },"t2");
        t1.start();
        t2.start();
        t1.join();
        t2.join();
        log.info("x的最终值：{}",x);

    }
}
