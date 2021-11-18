package cn.itcast.n1;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

@Slf4j(topic = "c.Test3")
public class Test3 {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        Callable<Integer>c3= () -> {
            Thread.sleep(3000);
            log.debug("t3");
            return 100;
        };
        FutureTask<Integer> task3=new FutureTask<>(c3);
        new Thread(task3,"t3").start();
        Integer result = task3.get();
        log.debug("返回结果是：{}",result);

    }


}
