package cn.itcast.n4;

import lombok.extern.slf4j.Slf4j;
/*
* 先2后1
* 限制性
*
* */
@Slf4j
public class Test3 {
    public static void main(String[] args) {
        Lock3 lock3 = new Lock3();
        Lock3 lock2 = new Lock3();
        new Thread(lock2::sub).start();
        new Thread(()-> {
            try {
                Lock3.add();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();



    }
}
@Slf4j
class Lock3{
    private static int count = 0;
    static synchronized void add() throws InterruptedException {
//        synchronized (this){
//            count++;
//        }
        Thread.sleep(1000);
        System.out.println(1);

    }

    public  synchronized  void sub(){
//        synchronized (this){
//            count--;
//        }
        System.out.println(2);

    }

     public synchronized int get(){
        return count;
    }

}
