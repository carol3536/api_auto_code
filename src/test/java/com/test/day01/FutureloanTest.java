package com.test.day01;
import org.testng.annotations.Test;
import static io.restassured.RestAssured.*;

/**
 * @Project: api_auto_test
 * @Site: http://www.lemonban.com
 * @Forum: http://testingpai.com
 * @Copyright: 2020 版权所有 湖南省零檬信息技术有限公司
 * @Author: Carol
 * @Create: 2020-12-16 14:52
 * @Desc：
 **/

public class FutureloanTest {
    @Test
    public void testRegister(){
        String jsonStr = "{\n" +
                "  \"mobile_phone\": \"15815341409\",\n" +
                "  \"pwd\": \"lemon66622\"\n" +
                "}";
               given().
                       contentType("application/json;charset=utf-8").
                       header("X-Lemonban-Media-Type","lemonban.v1").
                       body(jsonStr).

                when().
                        post("http://api.lemonban.com/futureloan/member/register").
                then().

                log().body();
    }
    @Test
    public void testLogin(){
        String jsonStr = "{\n" +
                "  \"mobile_phone\": \"15815341409\",\n" +
                "  \"pwd\": \"lemon66622\"\n" +
                "}";
        given().
                contentType("application/json;charset=utf-8").
                header("X-Lemonban-Media-Type","lemonban.v1").
                body(jsonStr).

                when().
                post("http://api.lemonban.com/futureloan/member/login").
                then().

                log().body();
    }
}
