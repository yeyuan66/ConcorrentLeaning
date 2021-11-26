package cn.itcast.JUCLeaning.aqs.readWriteTest;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.locks.StampedLock;

import static java.lang.Thread.sleep;

@Slf4j(topic = "c.StampedTest")
public class StampedTest {

    public static void main(String[] args) {
        DataContainerStamped dataContainer = new DataContainerStamped(1);
        new Thread(() -> {
            dataContainer.read(1);
        }, "t1").start();
        try {
            sleep((long) 0.5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        new Thread(() -> {
            dataContainer.read(0);
        }, "t2").start();
    }
}
@Slf4j(topic = "c.DataContainerStamped")
class DataContainerStamped{
    private int data;
    //时间戳锁
    private final StampedLock lock = new StampedLock();
    public DataContainerStamped(int data) {
        this.data = data;
    }

    //读取方法
    public int read(int readTime){
        //获取当前时间戳
        long stamp = lock.tryOptimisticRead();
        log.debug("optimistic read locking...{}", stamp);
        try {
            sleep(readTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //每次读取后提交前都要做检验，防止别人对数据进行修改，如果被修改升级锁
        if(lock.validate(stamp)){
            log.debug("read finish...{}, data:{}", stamp, data);
            return data;
        }

// 锁升级 - 读锁
        log.debug("updating to read lock... {}", stamp);
        try {
            stamp = lock.readLock();
            log.debug("read lock {}",stamp);
            try {
                sleep(readTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            log.debug("read finish...{}, data:{}", stamp, data);
            return data;
        }finally {
            log.debug("read unlock {}", stamp);
            lock.unlockRead(stamp);

        }

    }

    public void write(int newData) {
        long stamp = lock.writeLock();
        log.debug("write lock {}", stamp);
        try {
            try {
                sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            this.data = newData;
        } finally {
            log.debug("write unlock {}", stamp);
            lock.unlockWrite(stamp);
        }
    }


}
