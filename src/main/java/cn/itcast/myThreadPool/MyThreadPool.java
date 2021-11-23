package cn.itcast.myThreadPool;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j(topic = "c.MyThreadPool")
public class MyThreadPool {

    public static void main(String[] args) {
        ThreadPool threadPool = new ThreadPool(1,
                2,1, TimeUnit.MILLISECONDS,  (queue, task)->{
// 1. 死等
 queue.put(task);
// 2) 带超时等待
// queue.offer(task, 1500, TimeUnit.MILLISECONDS);
// 3) 让调用者放弃任务执行
// log.debug("放弃{}", task);
// 4) 让调用者抛出异常
// throw new RuntimeException("任务执行失败 " + task);
// 5) 让调用者自己执行任务
  //          task.run();
        });
        for (int i = 0; i < 4; i++) {
            int j = i;
            threadPool.execute(() -> {
                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                log.debug("{}", j);
            });
        }
    }
}

//拒绝策略接口，策略模式思想
@FunctionalInterface
interface RejectPolicy<T>{
    void reject(BlockingQueue<T>queue,T task);
}

@FunctionalInterface // 拒绝策略
interface RejectPolicy<T> {
    void reject(BlockingQueue<T> queue, T task);
}
//自定义阻塞队列
@Slf4j(topic = "c.BlockingQueue")
class BlockingQueue<T>{
    //队列
    Deque<T> queue = new ArrayDeque<>();
    //最大容量
    int capacity;

    public BlockingQueue(int capacity) {
        this.capacity = capacity;
    }

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
    //核心线程数
    private Integer coreSize;
    //核心线程集合
    private HashSet<Worker> workers= new HashSet<>();
    private long timeout;
    private TimeUnit timeUnit;
    private RejectPolicy<Runnable> rejectPolicy;
    public ThreadPool(int coreSize, long timeout, TimeUnit timeUnit, int queueCapcity,
                      RejectPolicy<Runnable> rejectPolicy) {
        this.coreSize = coreSize;
        this.timeout = timeout;
        this.timeUnit = timeUnit;
        this.blockingQueue = new BlockingQueue<>(queueCapcity);
        this.rejectPolicy = rejectPolicy;
    }

    public void execute(Runnable task){
        //判断核心线程数是否已满，未满添加，满了交给阻塞队列
        if(workers.size() < coreSize){
            Worker worker = new Worker(task);
            workers.add(worker);
            worker.start();
        }
        else {
            blockingQueue.put(task);
            //TODO 更改为选择拒绝策略的添加
        }
    }

    class Worker extends Thread{
        private Runnable task;
        Worker(Runnable task){
            this.task = task;
        }

        @Override
        public void run() {
            while (task != null || (task = blockingQueue.take())!=null){
                try {
                    log.debug("任务{}开始执行",task);
                    task.run();
                }catch (Exception e){
                    e.printStackTrace();
                }finally {
                    task = null;
                }

            }
        }
    }
}







