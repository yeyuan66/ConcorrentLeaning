package cn.itcast.account;

import java.math.BigDecimal;
import java.util.ArrayList;

public interface DecimalAccount {
    BigDecimal getBalance();
    void decrement(BigDecimal amount);

    static void work(DecimalAccount account){
        ArrayList<Thread> threads = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            Thread thread = new Thread(()->{
                account.decrement(BigDecimal.TEN);
            });
            threads.add(thread);

        }

        //这个语法很重要
        threads.forEach(Thread::start);

        threads.forEach(thread -> {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        System.out.println(account.getBalance());


    }


}
