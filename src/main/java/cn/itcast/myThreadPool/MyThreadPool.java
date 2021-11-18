package cn.itcast.myThreadPool;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j(topic = "c.MyThreadPool")
public class MyThreadPool {
}

//拒绝策略接口，策略模式思想
@FunctionalInterface
interface RejectPolicy<T>{
    void reject(BlockingQueue<T>queue,T task);
}

//自定义阻塞队列
class BlockingQueue<T>{
    public BlockingQueue(int capacity) {
        this.capacity = capacity;
    }

    //队列
    Deque<T> queue = new ArrayDeque<>();
    //最大容量
    int capacity;
    //锁
    ReentrantLock lock = new ReentrantLock();
    //队列添加条件变量
    Condition fullWaitSet = lock.newCondition();

    //队列获取条件变量
    Condition emptyWaitSet = lock.newCondition();

    //一直阻塞等待的获取
    public T take(){
        lock.lock();
       try{
           while (queue.isEmpty()){
               try {
                   fullWaitSet.await();
               } catch (InterruptedException e) {
                   e.printStackTrace();
               }
           }
           //获取队列的队头元素
           T t = queue.removeFirst();
           //有元素，增加条件变量
           emptyWaitSet.signal();
           return t;
       }finally {
           lock.unlock();
       }

    }

    //带有等待时间的获取n
    public T poll(long timeOut, TimeUnit timeUnit){
        lock.lock();
        try{
            //统一转换时间单位
            long nanos = timeUnit.toNanos(timeOut);
            while (queue.isEmpty()){
                //时间到，返回空
                if(nanos <= 0)return null;
                try {
                    //边等待边计算剩余时间
                    nanos = fullWaitSet.awaitNanos(nanos);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            //获取队列的队头元素
            T t = queue.removeFirst();
            //有元素，增加条件变量
            emptyWaitSet.signal();
            return t;
        }finally {
            lock.unlock();
        }

    }


    //阻塞添加
    public void put(T t){
        lock.lock();
        try {
            while (queue.size() == capacity){
                try {
                    fullWaitSet.await();
                }catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
            queue.addLast(t);
            emptyWaitSet.signal();
        }  finally {
            lock.unlock();
        }
    }

    //带有等待时间的阻塞添加
    public boolean offer(T t,long timeOut, TimeUnit timeUnit){
        lock.lock();
        try {
            long nanos = timeUnit.toNanos(timeOut);
            while (queue.size() == capacity){
                if(nanos <= 0)return false;
                try {
                    nanos = fullWaitSet.awaitNanos(nanos);
                }catch (InterruptedException e){
                    e.printStackTrace();
                }

            }
            queue.addLast(t);
            emptyWaitSet.signal();
            return true;
        } finally {
            lock.unlock();
        }
    }


}
@Slf4j(topic = "c.ThreadPool")
class ThreadPool{
    //阻塞队列
    private BlockingQueue<Runnable> blockingQueue;
    //任务集合 ，并发集合应该是final的

    private final Set<Worker> workerSet = new HashSet<>();
    //核心线程数
    private int coreSize;
    //最长等待时间
    private long TimeOut;
    //时间单位
    private TimeUnit timeUnit;

    private RejectPolicy<Runnable> rejectPolicy;
    public ThreadPool(int queueSize, int coreSize, long timeOut, TimeUnit timeUnit,RejectPolicy<Runnable> rejectPolicy) {
        this.blockingQueue = new BlockingQueue<>(queueSize);
        this.coreSize = coreSize;
        TimeOut = timeOut;
        this.timeUnit = timeUnit;
        this.rejectPolicy = rejectPolicy;
    }

    //TODO 线程池执行任务方法


    class Worker extends Thread{
         Runnable task;
        Worker(Runnable task){
            this.task = task;
        }

        @Override
        public void run() {
            //当前没有任务，从阻塞队列中获取
            while (task!=null || (task = blockingQueue.take())!=null){
                try{
                    log.debug("线程运行中：{}",task);
                    task.run();
                }catch (Exception e){
                    e.printStackTrace();
                }finally {
                    task =null;
                }
            }

            synchronized (workerSet){
                log.debug("{}从workers中移除",this);
                workerSet.remove(this);
            }


        }
    }
}







