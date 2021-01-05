package com.lemon.utils;

import java.util.Random;

/**
 * @Project: api_auto_test
 * @Site: http://www.lemonban.com
 * @Forum: http://testingpai.com
 * @Copyright: 2020 版权所有 湖南省零檬信息技术有限公司
 * @Author: Carol
 * @Create: 2020-12-28 10:11
 * @Desc：
 **/

public class ParamRandom {
    public static  String getParamRandom(){
        String RandomNum = "158";
        while (true){
            for (int i = 0; i < 8; i++) {
                Random random = new Random();
                int count = random.nextInt(9);
                RandomNum = RandomNum + count;
            }
            Object result = JDBCUtils.querySingle("select count(*) from member where mobile_phone = " + RandomNum);
            if ((Long) result ==1){
                System.out.println("号码已存在");
            }else{
                return RandomNum;
            }
        }

    }

    public static void main(String[] args) {

        System.out.println(getParamRandom());
    }
}
