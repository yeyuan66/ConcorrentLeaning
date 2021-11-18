package cn.itcast.n6;


import java.util.concurrent.atomic.AtomicInteger;

public class AtomicTest {
    public static void main(String[] args) {
        AtomicInteger atomicInteger = new AtomicInteger(0);
        Thread t1 = new Thread(() -> {

            for (int i = 0; i < 10; i++) {
                int andIncrement = atomicInteger.getAndIncrement();
                System.out.println(andIncrement);
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            System.out.println(atomicInteger);
        },"t1");
        t1.start();

        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                int i1 = atomicInteger.decrementAndGet();
                System.out.println(i1);
            }
            System.out.println(atomicInteger);
        },"t2");
        t2.start();

        System.out.println(atomicInteger);



    }
}
