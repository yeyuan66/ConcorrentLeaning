package cn.itcast.myThreadPool;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class MyThreadPool {
}

//自定义阻塞队列
class BlockingQueue<T>{
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







