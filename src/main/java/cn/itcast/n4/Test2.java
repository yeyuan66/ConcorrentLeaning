package cn.itcast.n4;

import lombok.extern.slf4j.Slf4j;

@Slf4j(topic = "c.Test2")
public class Test2 {

    public static void main(String[] args) {

        Lock lock = new Lock();
        for (int i = 0; i < 5000; i++) {
            lock.add();
        }
        for (int i = 0; i < 5000; i++) {
            lock.sub();
        }
        log.info("count:{}",lock.get());

    }
}

class Lock{
    private static int count = 0;
    static synchronized void add(){
//        synchronized (this){
//            count++;
//        }
        count++;
    }

    static  synchronized void sub(){
//        synchronized (this){
//            count--;
//        }
        count--;
    }

    static  synchronized int get(){
        return count;
    }

}


