package com.test.day02;

import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.*;

/**
 * @Project: api_auto_test
 * @Site: http://www.lemonban.com
 * @Forum: http://testingpai.com
 * @Copyright: 2020 版权所有 湖南省零檬信息技术有限公司
 * @Author: Carol
 * @Create: 2020-12-18 15:24
 * @Desc：
 **/

public class AssertTest {
    @Test
    public void testLogin(){
        String jsonStr = "{\n" +
                "  \"mobile_phone\": \"15815341409\",\n" +
                "  \"pwd\": \"lemon66622\"\n" +
                "}";

        Response res =
        given().
                contentType("application/json;charset=utf-8").
                header("X-Lemonban-Media-Type","lemonban.v1").
                body(jsonStr).

                when().
                post("http://api.lemonban.com/futureloan/member/login").
                then().
                log().all().extract().response();
//        获取业务的code
        int code = res.path("code");
//        获取业务的msg
        String msg = res.path("msg");
//        获取mobile_phone
        String mobilePhone = res.path("data.mobile_phone");
//        断言  Testng提供的断言API
//        第一个参数：实际值，第二个参数为期望值，第三个参数可选：断言失败的提示信息

        Assert.assertEquals(code,0);
        Assert.assertEquals(msg,"OK");
//        Assert.assertEquals(mobilePhone,"15815341409");
        Assert.assertEquals(mobilePhone,"15815341409","断言失败");
        Assert.assertTrue(code == 0);
        Assert.assertTrue(msg.equals("OK"));


    }
}
