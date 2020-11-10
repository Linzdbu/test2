package com.practise.threadExample;

import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class FutureDemo {

    public static void main(String[] args) {

        ExecutorService service = Executors.newFixedThreadPool(10);
        //线程池提交某一个任务 service.submit
        //任务是要implements Callable<Integer> 接口
        //将返回值，存储在future 里面
        Future<Integer> future = service.submit(new CallableTask());
        try {
            System.out.println(future.get());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        service.shutdown();
    }


    public static class CallableTask implements Callable<Integer> {

        @Override
        public Integer call() throws Exception {

            Thread.sleep(3000);
            return new Random().nextInt();
        }
    }

}
