package cn.itcast.JUCLeaning.aqs;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;


/*
* 自定义锁继承Lock，里面的方法都是必须要重写的
* */
@Slf4j(topic = "c.MyLock")
public class MyLock implements Lock {
    static MySync sync = new MySync();
    @Override
    public void lock() {

        //顶级入口，获取就进锁执行，否则进入等待队列
        sync.acquire(1);

    }

    //可打断获取
    @Override
    public void lockInterruptibly() throws InterruptedException {
        sync.acquireInterruptibly(1);
    }

    @Override
    public boolean tryLock() {
        //尝试一次，不成功直接返回，不进入队列
        return sync.tryAcquire(1);
    }

    //会进入等待队列，但有等待时限
    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return sync.tryAcquireNanos(1, unit.toNanos(time));
    }

    @Override
    public void unlock() {
        sync.release(1);
    }

    @Override
    public Condition newCondition() {
        return sync.newCondition();
    }

    public static void main(String[] args) {
        MyLock myLock = new MyLock();
        //测试不可重入
        new Thread(() -> {
            try {
                myLock.lock();
                log.debug("第一次进入");
                myLock.lock();
                log.debug("第二次进入");
                Thread.sleep(1000);
            }catch (InterruptedException e){
                e.printStackTrace();
            }finally {
                myLock.unlock();
            }

        }).start();

        new Thread(() -> {
            try {

                myLock.lock();
                log.debug("第二个线程进入");
                Thread.sleep(1000);
            }catch (InterruptedException e){
                e.printStackTrace();
            }finally {
                myLock.unlock();
            }

        }).start();




    }
}
