package com.test.day01;
import org.testng.annotations.Test;

import java.io.File;
import java.util.HashMap;

import static io.restassured.RestAssured.*;

/**
 * @Project: api_auto_test
 * @Site: http://www.lemonban.com
 * @Forum: http://testingpai.com
 * @Copyright: 2020 版权所有 湖南省零檬信息技术有限公司
 * @Author: Carol
 * @Create: 2020-12-15 20:02
 * @Desc：
 **/

public class RestApiTest {
    @Test
    public void testGet01() {
//        given 配置参数，请求头，请求参数，请求数据
               given().
//        when 是用来发起请求（get/post）
                when().
                        get("http://httpbin.org/get").

                then().
//                       对应结果做什么事情
                        log().all();
    }
    @Test
    public void testGet02() {
//        given 配置参数，请求头，请求参数，请求数据
        given().
                queryParam("name","carol").queryParam("age","20").
//        when 是用来发起请求（get/post）
        when().
                get("http://httpbin.org/get").

                then().
//                       对应结果做什么事情
        log().all();
    }
    @Test
    public void testGet03() {
//        given 配置参数，请求头，请求参数，请求数据
        given().

//        when 是用来发起请求（get/post）
        when().
                get("http://httpbin.org/get?name=carol&age=12").

                then().
//                       对应结果做什么事情
        log().all();
    }
    @Test
    public void testPot01() {
//        given 配置参数，请求头，请求参数，请求数据
//        form表单类型
        given().
                formParam("name","carol").
                contentType("application/x-www-form-urlencoded;charset=utf-8").
//        when 是用来发起请求（get/post）
        when().
                post("http://httpbin.org/post").

                then().
//                       对应结果做什么事情
        log().body();
    }
    @Test
    public void testPost02() {
//        given 配置参数，请求头，请求参数，请求数据
//        json参数类型
        String json = "{\"name\": \"carol\", \"age\": \"12\"}";
        HashMap<String,String> map = new HashMap<String, String>();
        map.put("name","carol");
        map.put("age","12");
        given().
                contentType("application/json;charset=utf-8").
                body(map).
//        when 是用来发起请求（get/post）
        when().
                post("http://httpbin.org/post").

                then().
//                       对应结果做什么事情
        log().body();
    }
    @Test
    public void testPot03() {
//        given 配置参数，请求头，请求参数，请求数据
//        text/xml参数类型
        String xml = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n" +
                "<TextScreen Timeout=\"15\">\n" +
                "\t<Title>Title name</Title>\n" +
                "\t<Text>What is STUN?\n" +
                "\tBy Nate Rand\n" +
                "\t</Text>\n" +
                "</TextScreen>";
        given().
                contentType("text/xml;charset=utf-8").
                body(xml).
//        when 是用来发起请求（get/post）
        when().
                post("http://httpbin.org/post").

                then().
//                       对应结果做什么事情
        log().body();
    }
    @Test
    public void testPot04() {
//        given 配置参数，请求头，请求参数，请求数据
//       多参数表单类型
        given().
                contentType("multipart/form-data;charset=utf-8").
                multiPart(new File("D:\\study\\test.png")).
//        when 是用来发起请求（get/post）
        when().
                post("http://httpbin.org/post").

                then().
//                       对应结果做什么事情
        log().body();
    }
}
