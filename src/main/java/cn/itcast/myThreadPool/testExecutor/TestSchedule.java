package cn.itcast.myThreadPool.testExecutor;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j(topic = "c.TestSchedule")
public class TestSchedule {
    public static void main(String[] args) {

        //定时线程池可以让定时任务调度并行执行，互不影响，不像timer那样只能串行
        //特点：线程数固定，任务数多于线程数时，会放入无界队列排队。任务执行完毕，这些线
        //程也不会被释放。用来执行延迟或反复执行的任务
        ScheduledExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(2);
        scheduledThreadPool.execute(() -> {
            log.debug("线程1开始执行");
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            log.debug("线程1执行完毕");
        });

        scheduledThreadPool.execute(() -> {
            log.debug("线程2开始执行");
            log.debug("线程2执行完毕");
        });

        testFixedRate();
        testFixedRate2();
    }


    //固定时间间隔进行执行
    public static void testFixedRate(){
        ScheduledExecutorService pool = Executors.newScheduledThreadPool(1);
        log.debug("start...");

        //initialDelay:第一次执行等待
        //period: 执行周期
        pool.scheduleAtFixedRate(() -> {
            log.debug("running...");
        }, 1, 1, TimeUnit.SECONDS);

    }


    //如果线程执行时间超过了时间间隔，时间间隔失效
    public static void testFixedRate2(){
        ScheduledExecutorService pool = Executors.newScheduledThreadPool(1);
        log.debug("start...");
        pool.scheduleAtFixedRate(() -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            log.debug("running...");
        }, 1, 1, TimeUnit.SECONDS);

        /*
        * 3.scheduleWithFixedDelay 的间隔是 上一个任务结束 <-> 延时 <-> 下一个任务开始
         * */
    }


}
