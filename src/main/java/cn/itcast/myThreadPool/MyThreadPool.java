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
        ThreadPool threadPool = new ThreadPool(2,
                20,TimeUnit.MILLISECONDS, 2, (queue, task)->{
// 1. 死等
// queue.put(task);
// 2) 带超时等待，如果超时了，让调用者自己执行
 if(!queue.offer(task, 15, TimeUnit.MILLISECONDS))task.run();
// 3) 让调用者放弃任务执行
// log.debug("放弃{}", task);
// 4) 让调用者抛出异常
// throw new RuntimeException("任务执行失败 " + task);
// 5) 让调用者自己执行任务
  //          task.run();
        });
        for (int i = 0; i < 5; i++) {
            int j = i;
            threadPool.execute(() -> {
                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                log.debug("执行线程：打印{}", j);
            });
        }
    }
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
                   log.debug("队列为空，进入死等");
                   emptyWaitSet.await();
               } catch (InterruptedException e) {
                   e.printStackTrace();
               }
           }
           //获取队列的队头元素
           T t = queue.removeFirst();
           //唤醒等待空位的元素
           log.debug("获取队头元素:{}",t);
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
                if(nanos <= 0){
                    log.debug("等待获取时间结束，返回空");
                    return null;
                }
                log.debug("队列空，进入限时等待");
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
            log.debug("获取队头元素:{}",t);
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
                    log.debug("队列已满，进入死等:{}",t);
                    fullWaitSet.await();
                }catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
            queue.addLast(t);
            log.debug("加入队列:{}",t);
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
                if(nanos <= 0){
                    log.debug("添加超时：{}",t);
                    return false;
                }
                log.debug("队列满，进入限时等待：{}",t);
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

    public void tryPut(RejectPolicy<T> rejectPolicy,T task){
        //记得一定一定要加锁！
        lock.lock();
        //当队列元素未满直接加入，否则根据策略执行
        try {
            if (queue.size() < capacity){
                log.debug("加入任务队列：{}",task);
                queue.addLast(task);
                //加入后唤醒空等
                emptyWaitSet.signal();


            }
            else rejectPolicy.reject(this,task);
        }finally {
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
    //核心线程集合，设置为final是说不能改变引用的指向，但里面的元素可以改变
    private final HashSet<Worker> workers= new HashSet<>();
    private long timeout;
    private TimeUnit timeUnit;
    //拒绝策略
    private RejectPolicy<Runnable> rejectPolicy;
    public ThreadPool(int coreSize, long timeout, TimeUnit timeUnit, int queueCapacity,
                      RejectPolicy<Runnable> rejectPolicy) {
        this.coreSize = coreSize;
        this.timeout = timeout;
        this.timeUnit = timeUnit;
        this.blockingQueue = new BlockingQueue<>(queueCapacity);
        this.rejectPolicy = rejectPolicy;
    }

    public void execute(Runnable task){
        synchronized (workers){
            log.debug("产生新任务：{}",task);
            //判断核心线程数是否已满，未满添加，满了交给阻塞队列
            if(workers.size() < coreSize){

                Worker worker = new Worker(task);
                workers.add(worker);
                worker.start();
            }
            else {
                //blockingQueue.put(task);
                // 更改为选择拒绝策略的添加

                blockingQueue.tryPut(rejectPolicy,task);
            }
        }

    }

    class Worker extends Thread{
        private Runnable task;
        Worker(Runnable task){
            this.task = task;
        }

        @Override
        public void run() {
            //执行完毕从阻塞队列获取新的线程作为核心，并且是超时获取
            while (task != null || (task = blockingQueue.poll(timeout,timeUnit))!=null){
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







