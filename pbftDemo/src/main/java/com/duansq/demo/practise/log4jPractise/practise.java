package com.duansq.demo.practise.log4jPractise;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class practise {


    public static void main(String[] args) {

        Logger logger = LoggerFactory.getLogger(practise.class);
        System.out.println(logger.getClass());
        System.out.println(logger.getName());
        System.out.println("=======================");

        Logger logger2 = LoggerFactory.getLogger(Object.class);
        System.out.println(logger2.getClass());
        System.out.println(logger2.getName());


    }


}
