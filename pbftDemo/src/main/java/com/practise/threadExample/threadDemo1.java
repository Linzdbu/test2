package com.practise.threadExample;

public class threadDemo1 {

    //第一种使用线程的方式
    public static void main(String[] args) {
        //创建线程对象
        AThread a = new AThread();
        BThread b = new BThread();
        CThread c = new CThread();
        DThread d = new DThread();

        a.start();
        b.start();
        c.start();
        d.start();
    }

}

//自定义线程类，继承Thread类
class AThread extends Thread {

    //重写run()方法
    public void run() {

        for (int i = 0; i < 10; i++) {
            System.out.println("这是线程A，出现了第" + (i + 1) + "次");
        }
    }


}

//extends Thread  继承线程类
class BThread extends Thread {

    public void run() {
        for (int i = 0; i < 10; i++) {
            System.out.println("这是线程B，出现了第" + (i + 1) + "次");
        }
    }

}

class CThread extends Thread {

    public void run() {

        for (int i = 0; i < 10; i++) {
            System.out.println("这是线程C，出现了第" + (i + 1) + "次");
        }
    }

}

class DThread extends Thread {

    public void run() {
        for (int i = 0; i < 10; i++) {
            System.out.println("这是线程D，出现了第" + (i + 1) + "次");
        }
    }

}
