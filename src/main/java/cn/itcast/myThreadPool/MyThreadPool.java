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

//自定义阻塞队列
@Slf4j(topic = "c.BlockingQueue")
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
                   emptyWaitSet.await();
               } catch (InterruptedException e) {
                   e.printStackTrace();
               }
           }
           //获取队列的队头元素
           T t = queue.removeFirst();
           log.debug("从任务队列移除： {} ...", t);
           //有元素，增加条件变量
           fullWaitSet.signal();
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
                    nanos = emptyWaitSet.awaitNanos(nanos);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            //获取队列的队头元素
            T t = queue.removeFirst();
            //有元素，增加条件变量
            fullWaitSet.signal();
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
                    log.debug("等待加入任务队列 {} ...", t);
                    fullWaitSet.await();
                }catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
            log.debug("加入任务队列 {}", t);
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
                    log.debug("等待加入任务队列 {} ...", t);
                    nanos = fullWaitSet.awaitNanos(nanos);
                }catch (InterruptedException e){
                    e.printStackTrace();
                }

            }
            log.debug("加入任务队列 {}", t);
            queue.addLast(t);
            emptyWaitSet.signal();
            return true;
        } finally {
            lock.unlock();
        }
    }

    //设置策略的队列添加
    public void tryPut(RejectPolicy<T> rejectPolicy,T task){
        lock.lock();
        try {
            if(queue.size() < capacity){
                queue.addLast(task);
                emptyWaitSet.signal();
            }
            else {
                log.debug("加入任务队列 {}", task);
                rejectPolicy.reject(this,task);
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
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


    public void execute(Runnable task){
        //如果当前核心数未达到上限，创建核心线程
        //必须互斥操作核心线程集合，可以用并发集合优化
        synchronized (workerSet){
            if(workerSet.size() < coreSize){
                Worker worker = new Worker(task);
                log.debug("新增 worker{}, {}", worker, task);
                workerSet.add(worker);
                worker.start();
            }
            else {
    //            blockingQueue.put(task);
                //自定义等待策略
                //1. 死等
                //2. 超时等待
                //3. 抛出异常
                //4. 放弃执行
                //5. 调用者线程自己执行

                rejectPolicy.reject(blockingQueue,task);
            }
        }
    }

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







