package com.test.day01;

import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;

/**
 * @Project: api_auto_test
 * @Site: http://www.lemonban.com
 * @Forum: http://testingpai.com
 * @Copyright: 2020 版权所有 湖南省零檬信息技术有限公司
 * @Author: Carol
 * @Create: 2020-12-16 15:22
 * @Desc：
 **/

public class homework02 {
    @Test
    public void test01(){
//        注册
        System.out.println("------------注册--------------");
        HashMap<String,String> map = new HashMap<String, String>();
        map.put("mobile_phone","1557894609");
        map.put("pwd","lemon66622");
        map.put("type","");
        given().
                contentType("application/json;charset=utf-8").
                header("X-Lemonban-Media-Type","lemonban.v2").
                body(map).
        when().
                post("http://api.lemonban.com/futureloan/member/register").
         then().
                log().all();
        System.out.println("----------登录--------------");
//登录
        Response res =
        given().
                contentType("application/json;charset=utf-8").
                header("X-Lemonban-Media-Type","lemonban.v2").
                body(map).

                when().
                post("http://api.lemonban.com/futureloan/member/login").
                then().log().all().

                extract().response();
//        提取响应体、
//        提取响应结果对应字段值token
//        path方法-----使用Gpath路径表达式语法来提取
        String tokenValue = res.path("data.token_info.token");
        System.out.println(tokenValue);

//        提取会员ID
        int memberId = res.path("data.id");
        System.out.println(memberId);

//        充值请求
//        把数据放到map中
        System.out.println("--------------------充值-------------");
        Map<String,String> mapRecharge = new HashMap<String, String>();
        mapRecharge.put("member_id",memberId + "");
        mapRecharge.put("amount","10000");
        given().
                contentType("application/json;charset=utf-8").
                header("X-Lemonban-Media-Type","lemonban.v2").
//                按照接口文档规定，如果鉴权的法式lemonban.v2,那么就要加token加请求
                header("Authorization","Bearer "+ tokenValue).
                body(mapRecharge).

                when().
                post("http://api.lemonban.com/futureloan/member/recharge").
                then().log().all().

                extract().response();

//        新增项目
        System.out.println("-----------------新增--------------------");
        Map<String,String> mapAdd = new HashMap<String, String>();
        mapAdd.put("member_id",memberId + "");
        mapAdd.put("title","aaaa");
        mapAdd.put("amount","10000");
        mapAdd.put("loan_rate","18.0");
        mapAdd.put("loan_term","6");
        mapAdd.put("loan_date_type","1");
        mapAdd.put("bidding_days","5");
        Response res2 =
        given().
                contentType("application/json;charset=utf-8").
                header("X-Lemonban-Media-Type","lemonban.v2").
//                按照接口文档规定，如果鉴权的法式lemonban.v2,那么就要加token加请求
        header("Authorization","Bearer "+ tokenValue).
                body(mapAdd).

                when().
                post("http://api.lemonban.com/futureloan/loan/add").
                then().log().all().

                extract().response();


        int loanId = res2.path("data.id");
        System.out.println(loanId);

        //        投资项目
        System.out.println("-----------投资---------------");
        Map<String,Integer> mapInvest = new HashMap<String, Integer>();
        mapInvest.put("member_id",memberId);
        mapInvest.put("loan_id",loanId);
        mapInvest.put("amount",6000);
        given().
                contentType("application/json;charset=utf-8").
                header("X-Lemonban-Media-Type","lemonban.v2").
//                按照接口文档规定，如果鉴权的法式lemonban.v2,那么就要加token加请求
        header("Authorization","Bearer "+ tokenValue).
                body(mapAdd).

                when().
                post("http://api.lemonban.com/futureloan/member/invest").
                then().log().all().

                extract().response();
    }
}

