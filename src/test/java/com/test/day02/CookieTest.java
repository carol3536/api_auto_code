package com.test.day02;

import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.*;

/**
 * @Project: api_auto_test
 * @Site: http://www.lemonban.com
 * @Forum: http://testingpai.com
 * @Copyright: 2020 版权所有 湖南省零檬信息技术有限公司
 * @Author: Carol
 * @Create: 2020-12-18 13:47
 * @Desc：
 **/

public class CookieTest {
    Map<String,String> cookieMap = new HashMap<String, String>();
    @Test(priority = 1)
    public void testAuthenticationWithSession(){
//        登录接口请求
        Response res =
        given().
                header("Content-Type","application/x-www-form-urlencoded; charset=UTF-8").
                header("X-Lemonban-Media-Type","lemonban.v2").
                formParam("loginame","admin").formParam("password","e10adc3949ba59abbe56e057f20f883e").
         when().
                post("http://erp.lemfix.com/user/login ").
         then().

         log().all().extract().response();
//        需要分割然后取出set-cookie
        System.out.println(res.header("set-cookie"));

//        推荐使用
        cookieMap = res.getCookies();
        System.out.println(cookieMap);
    }
    @Test(priority = 2)
    public void testxx(){
                given().
                        cookies(cookieMap).

                when().
                       get("http://erp.lemfix.com/user/getUserSession").
                       then().

                         log().all();
    }
}
