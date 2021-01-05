package com.lemon.util;/*
author:carol
**/

import java.util.Random;

public class PhoneRandom {
    public static String getPhoneRandom() {
        String phoneNum = "158";
        while (true) {
        for (int i = 0; i < 8; i++) {
            Random random = new Random();
            int num = random.nextInt(9);
            phoneNum = phoneNum + num;
        }

            Object result = JDBCUtils.querySingle("select count(*) from member where mobile_phone = " + phoneNum);
            System.out.println(result);
            if ((Long) result == 1) {
                System.out.println("已注册过");
            } else {
                return phoneNum;
            }
        }
    }

    public static void main(String[] args) {


        System.out.println(getPhoneRandom());
    }

}
