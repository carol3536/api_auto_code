package com.lemon.testcases;/*
author:carol
**/

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lemon.base.BaseCase;
import com.lemon.pojo.CaseInfo;
import io.qameta.allure.Allure;
import io.restassured.RestAssured;
import io.restassured.config.JsonConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.path.json.config.JsonPathConfig;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static io.restassured.RestAssured.given;
import static io.restassured.config.JsonConfig.jsonConfig;

public class Recharge extends BaseCase {
    List<CaseInfo> caseInfoList;
    @BeforeClass
    public void setup(){

        caseInfoList = getCaseDataFromExcel(4);
        caseInfoList = paramReplace(caseInfoList);
    }


    @Test(dataProvider = "getRechargeDatas")
    public void testRecharge(CaseInfo caseInfo) throws JsonProcessingException, FileNotFoundException {
//        参数化替换
//        1.接口URL地址{{memberId}}给替换成环境变量中保存的值

//        2.响应结果中{{memberId}}给替换成环境变量中保存的值
//        ObjectMapper objectMapper = new ObjectMapper();
//        Map headerMap = objectMapper.readValue(caseInfo.getRequestHeader(), Map.class);
        Map headerMap = fromJsonToMap(caseInfo.getRequestHeader());
        System.out.println(headerMap);
        String logToFile = addLogToFile(caseInfo.getInterfaceName(), caseInfo.getCaseId());
        Response res =
                given().
//                        让REST-Assured返回json小数的时候，使用BigDecimal类型来存储小数（默认时Float存储的）
                        config(RestAssured.config().jsonConfig(jsonConfig().numberReturnType(JsonPathConfig.NumberReturnType.BIG_DECIMAL))).
                        headers(headerMap).
                        body(caseInfo.getInputParams()).
                when().
                        post( caseInfo.getUrl()).

                then().
                        log().body().

                        extract().response();
        InputStream inputStream = new FileInputStream(logToFile);
        Allure.addAttachment("请求接口响应信息",inputStream);
//        String expected1 = caseInfo.getExpected();
//
//        ObjectMapper mapper2 = new ObjectMapper();
//        Map expectedMap = mapper2.readValue(expected1, Map.class);
//        Set<Map.Entry<String,Object>> set = expectedMap.entrySet();
//        for (Map.Entry<String, Object> map : set) {
////           关键点：做断言。通过Gpath获取实际接口响应对应字段的值
////            我们在Excel里面写用例的期望结果时，期望结果里面键名--->Gpath表达式
////            期望结果里面键值--->期望值
//            System.out.println("期望"+map.getValue().getClass());
//            System.out.println( "实际"+res.path(map.getKey()).getClass());
//            Object expected = map.getValue();
//
//            if (expected instanceof Float || expected instanceof Double){
//                BigDecimal bigDecimalData = new BigDecimal(expected.toString());
//                Assert.assertEquals(res.path(map.getKey()),bigDecimalData);
//            }else {
//                Assert.assertEquals(res.path(map.getKey()),expected);
//            }
//        }
        assertExpected(caseInfo,res);
        assertSqlExpected(caseInfo);
    }

    @DataProvider
    public Object[] getRechargeDatas(){

        return caseInfoList.toArray();
    }
    public static void main(String[] args) {
        //Double ？ double？？
        Double a = 0.01;
        Float b = 0.01f;
        if(a instanceof Double){
            System.out.println("是的");
        }
        //类型不一致会导致断言失败
        //BigDecimal -->大的小数 用它来进行运算可以避免精度的丢失（金额）
        //把原始的类型Float/Double转化成为BigDecimal
        //rest-assured如果接口响应结果返回是json，并且json里面有小数，你用Gpath表达式获取结果的时候用Float来存储
        //1、解决方案：Gpath表达式获取结果的时候用BigDecimal来存储（实际值）
//        2、把期望值也转化成BigDecimal
        BigDecimal bigDecimala = new BigDecimal(a.toString());
        BigDecimal bigDecimalb = new BigDecimal(b.toString());
        Assert.assertEquals(bigDecimala,bigDecimalb);
    }

}
