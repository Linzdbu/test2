package com.practise.threadExample;

import java.util.concurrent.*;

public class MyExecutorDemo {

    private static int MAX = 10;

    public static void main(String[] args) {


    }


    //静态方法；可以throws 两个异常
    public static void fixedThreadPool(int coreSize) throws InterruptedException, ExecutionException {

        ExecutorService exec = Executors.newFixedThreadPool(coreSize);
        for (int i = 0; i < MAX; i++) {

            //提交任务
            Future<Integer> task = exec.submit(new Callable<Integer>() {
                @Override
                public Integer call() throws Exception {
                    return null;
                }
            });
        }

    }


}
