package com.duansq.demo.pbft;

public class convertDate {


    public static int calculateTime(String str1, String str2) {

        String time1 = str1.substring(str1.length() - 6);
        String time2 = str2.substring(str2.length() - 6);
        return calMillerTime(time1, time2);

    }

    public static int calMillerTime(String str1, String str2) {

        int temp1 = (Integer.parseInt(String.valueOf(str1.charAt(0))) * 10 +
                Integer.parseInt(String.valueOf(str1.charAt(1)))) * 1000 +
                Integer.parseInt(str1.substring(3));
        int temp2 = (Integer.parseInt(String.valueOf(str2.charAt(0))) * 10 +
                Integer.parseInt(String.valueOf(str2.charAt(1)))) * 1000 +
                Integer.parseInt(str2.substring(3));

        return temp2 - temp1;
    }


}
