package com.practise.threadExample;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class excutorDemo {

    public static void main(String[] args) {

        //ExcutorService 里面的newCachedThreadPool 可缓存的线程池
        //不会对线程池的大小有限制
        System.out.println("缓存线程池");
        ExecutorService cacheThreadPool = Executors.newCachedThreadPool();
        for (int i = 0; i < 10; i++) {
            final int j = i;
            try {
                //使用Thread.sleep 方法必须抛异
                Thread.sleep(j * 1);
                //Thread 需要抛出中断异常
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            cacheThreadPool.execute(() -> System.out.println("线程名称：" + Thread.currentThread().getName() + "，执行" + j));

        }
        System.out.println("==========================================");

        //单线程线程池
        System.out.println("单线程线程池");
        ExecutorService pool = Executors.newSingleThreadExecutor();
        for (int i = 0; i < 10; i++) {
            final int j = i;
            try {
                Thread.sleep(j * i);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            pool.execute(() -> System.out.println("线程名称:" + Thread.currentThread().getName() + ",执行" + j));
        }
        System.out.println("======================================");
    }


}
