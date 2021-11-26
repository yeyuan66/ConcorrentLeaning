package cn.itcast.JUCLeaning.aqs;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Semaphore;

import static java.lang.Thread.sleep;

@Slf4j(topic = "c.SemaphoreTest")
public class SemaphoreTest {

    //可以看到，并发运行的任务数量不超过3个。
    public static void main(String[] args) {
        Semaphore semaphore = new Semaphore(3);
        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                //获取许可
                try {
                    semaphore.acquire();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                try {
                    log.debug("running");
                    try {
                        sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    log.debug("end...");
                }finally {
                    //释放许可
                    semaphore.release();
                }

            }).start();
        }
    }


}
