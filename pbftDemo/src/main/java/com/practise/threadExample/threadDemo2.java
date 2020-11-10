package com.practise.threadExample;

public class threadDemo2 {


    public static void main(String[] args) {

        //创建自定义对象
        ThreadA a = new ThreadA();
        ThreadB b = new ThreadB();
        ThreadC c = new ThreadC();
        ThreadD d = new ThreadD();

        //创建Thread 启动对象
        Thread t1 = new Thread(a);
        Thread t2 = new Thread(b);
        Thread t3 = new Thread(c);
        Thread t4 = new Thread(d);

        Thread test = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 10; i++) {
                    System.out.println("这是匿名线程类,出现了" + (i + 1) + "次");
                }
            }
        });


        test.start();
        t1.start();
        t2.start();
        t3.start();
        t4.start();
    }

}

//创建自定义类，实现Runnable接口
class ThreadA implements Runnable {

    @Override
    public void run() {
        for (int i = 0; i < 10; i++) {
            System.out.println("这是线程A,出现了第" + (i + 1) + "次");
        }

    }
}

class ThreadB implements Runnable {

    @Override
    public void run() {
        for (int i = 0; i < 10; i++) {
            System.out.println("这是线程B,出现了第" + (i + 1) + "次");
        }
    }
}

class ThreadC implements Runnable {


    @Override
    public void run() {
        for (int i = 0; i < 10; i++) {
            System.out.println("这是线程C,出现了第" + (i + 1) + "次");
        }
    }
}


class ThreadD implements Runnable {

    @Override
    public void run() {
        for (int i = 0; i < 10; i++) {
            System.out.println("这是线程D,出现了第" + (i + 1) + "次");
        }
    }
}

