package cn.itcast.n4;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Vector;

@Slf4j(topic = "c.TicketTest")
public class TicketTest {
    public static void main(String[] args) throws InterruptedException {

        TicketSeller ticketSeller = new TicketSeller(10000);

        List<Thread> threadList = new ArrayList<>();

        List<Integer> sellList = new Vector<>();
        for (int i = 0; i < 2000; i++) {
            Thread thread = new Thread(() -> {
                int sell = ticketSeller.sell(getRandom());
                sellList.add(sell);
            });
            thread.start();
            threadList.add(thread);

        }

        for (Thread thread : threadList) {
            thread.join();
        }
        int left = ticketSeller.getCount();
        int sellSum = sellList.stream().mapToInt(i -> i).sum();
        log.debug("剩余：{}",left);

        log.debug("卖出：{}",sellSum);

    }

    private static Random random = new Random();
    private static int getRandom(){
        return random.nextInt(5)+1;
    }


}

class TicketSeller{
    private int count;

    public TicketSeller(int count) {
        this.count = count;
    }

    public int getCount() {
        return count;
    }

    //卖出
    public int sell(int account){

        if(this.count>=account){
            this.count -= account;
            return account;
        }
        return 0;

    }
}
