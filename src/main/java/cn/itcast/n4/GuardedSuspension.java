package cn.itcast.n4;

import lombok.extern.slf4j.Slf4j;

@Slf4j(topic = "c.GuardedSuspension")
public class GuardedSuspension {
    public static void main(String[] args) {
        GuardObject guardObject = new GuardObject();

        //开启一个线程，等待另一个线程提供所需对象，并打印
        new Thread(() -> {
            log.info("等待下载");
            Object o = guardObject.get();
            log.info("返回的结果：{}",o);
        },"t1").start();

        //另外一个线程获取提供的对象
        new Thread(() -> {
            log.info("开始下载");
            Integer download = Downloader.download();
            guardObject.complete(download);

        },"t2").start();
    }
}

class GuardObject{
    private Object response;
    private final Object lock = new Object();
    public Object get(){
        //共享变量应该加锁
        //死循环等待传递结果

        synchronized (lock){
            while(response == null){
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return response;
        }

    }

    public void complete(Object response){
        synchronized (lock){
            this.response = response;
            //送来结果后唤醒等待线程
            lock.notifyAll();
        }
    }
}
