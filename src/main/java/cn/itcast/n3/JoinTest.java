package cn.itcast.n3;

import lombok.extern.slf4j.Slf4j;

@Slf4j(topic = "c.JoinTest")
public class JoinTest {


    public static int r =0;
    public static void main(String[] args) throws InterruptedException {
        test_Interrupt();
    }
    public static void test1() throws InterruptedException {
        Thread t1 = new Thread(() -> {
            log.debug("开始");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            log.debug("结束");
            r=10;
        });
        t1.start();
        //等待ti进程执行结束再继续执行
        t1.join();
        log.debug("r = {}",r);

    }

    //sleep中的线程为阻塞状态，也就是time_waiting,调用interrupt会修改打断标记
    public static void test_Interrupt() throws InterruptedException{
        Thread t2 = new Thread(() -> {

            log.debug("开始");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            log.debug("结束");
        }, "t2");
        t2.start();
        log.debug("t2-interruput:{}",t2.isInterrupted());
        Thread.sleep(500);
        //休眠一半打断
        t2.interrupt();
        Thread.sleep(1000);
        log.debug("t2-interruput:{}",t2.isInterrupted());
        /*
        * 最后输出false，因此sleep中的线程被打断会重新设置打断标记
        *
        * */


    }

    public static void test_Interrupt1() throws InterruptedException{
        Thread t3 = new Thread(() -> {
            while (true){
                Thread currentThread = Thread.currentThread();
                boolean interrupted = currentThread.isInterrupted();
                if(interrupted){
                    log.info("当前打断状态：{}",interrupted);
                    break;
                }

            }

        }, "t3");
        t3.start();
        log.debug("interruput:{}",t3.isInterrupted());
        Thread.sleep(500);
        //休眠一半打断
        t3.interrupt();

        log.debug("interruput:{}",t3.isInterrupted());
        /*
         * 最后输出false，因此运行中的线程被打断不会重设标记
         *
         * */

    }

}
