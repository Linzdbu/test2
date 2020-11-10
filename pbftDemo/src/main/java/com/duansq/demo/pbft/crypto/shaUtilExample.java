package com.duansq.demo.pbft.crypto;

import com.niudong.demo.util.SHAUtil;

public class shaUtilExample {

    public static void main(String[] args) {

        String test = "这是一个hash密码的测试原语句";
        String afterHash = SHAUtil.getSHA256BasedMD(test);
        System.out.println(afterHash);

        //两个不同工具包内的同一个hash密码
        String test2 = "这是一个hash密码的测试原语句";
        String aftetHash2 = SHAUtil.sha256BasedHutool(test2);
        System.out.println(aftetHash2);

       /* String test2 = "这是一个hash密码的测试语句";
        String afterHash2 = SHAUtil.getSHA256BasedMD(test2);
        System.out.println(afterHash2);*/
    }
}
