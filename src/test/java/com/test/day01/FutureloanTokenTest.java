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

public class FutureloanTokenTest {
    @Test
    public void testLogin(){
        String jsonStr = "{\n" +
                "  \"mobile_phone\": \"15815341409\",\n" +
                "  \"pwd\": \"lemon66622\"\n" +
                "}";
        Response res =
        given().
                contentType("application/json;charset=utf-8").
                header("X-Lemonban-Media-Type","lemonban.v2").
                body(jsonStr).

                when().
                post("http://api.lemonban.com/futureloan/member/login").
                then().log().all().

                extract().response();
//        获取到响应信息里面所有的内容：响应头+响应体
        System.out.println(res.asString());
        System.out.println("------------");
//        获取接口响应时间单位为ms
        System.out.println(res.time());
//        提取响应状态码
        System.out.println(res.statusCode());
//        获取响应头信息
        System.out.println(res.header("Content-type"));
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
        Map<String,String> map = new HashMap<String, String>();
        map.put("member_id",memberId + "");
        map.put("amount","10000");
        given().
                contentType("application/json;charset=utf-8").
                header("X-Lemonban-Media-Type","lemonban.v2").
//                按照接口文档规定，如果鉴权的法式lemonban.v2,那么就要加token加请求
                header("Authorization","Bearer "+ tokenValue).
                body(map).

                when().
                post("http://api.lemonban.com/futureloan/member/recharge").
                then().log().all().

                extract().response();

    }
}

