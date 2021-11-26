package cn.itcast.JUCLeaning.aqs.readWriteTest;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.locks.ReentrantReadWriteLock;

import static java.lang.Thread.sleep;

@Slf4j(topic = "c.ReadWriteLockTest")
public class ReadWriteLockTest {
    public static void main(String[] args) throws InterruptedException {
        DataContainer dataContainer = new DataContainer();
        //读锁不阻塞
        /*new Thread(() -> {
            try {
                dataContainer.read();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "t1").start();*/
        /*new Thread(() -> {
            try {
                dataContainer.read();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "t2").start();*/



        //别人获取的写锁会阻塞自己的读取
        new Thread(() -> {
            try {
                dataContainer.write();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        },"t3").start();
        Thread.sleep(100);
        new Thread(() -> {
            try {
                dataContainer.read();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "t4").start();

        //要么读完后写，要么写完后读

    }
}
@Slf4j(topic = "c.DataContainer")
class DataContainer{
    //要操作的数据
    private Object data;
    //读写锁
    private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    //读锁
    private ReentrantReadWriteLock.ReadLock readLock = lock.readLock();
    //写锁
    private ReentrantReadWriteLock.WriteLock writeLock = lock.writeLock();

    public Object read() throws InterruptedException {
        readLock.lock();
        try {
            log.debug("begin read");
            sleep(1000);

            return data;

        } finally {
            log.debug("释放读锁");
            readLock.unlock();
        }


    }
    public void write() throws InterruptedException {
        log.debug("获取写锁...");
        writeLock.lock();
        try {
            log.debug("写入");
            Thread.sleep(3000);
        } finally {
            log.debug("释放写锁...");
            writeLock.unlock();
        }
    }

}
