package cn.itcast.n3;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j(topic = "c.DaemonTest")
public class DaemonTest {
    public static void main(String[] args) throws InterruptedException {
        Thread thread = new Thread("t1"){
            @SneakyThrows
            @Override
            public void run() {
                log.info("开始");
                Thread.sleep(1000);
                log.info("守护线程提前结束，无法打印");
            }
        };
        thread.setDaemon(true);
        thread.start();
        Thread.sleep(500);
        log.info("所有非守护线程结束，程序终止");
    }
}
