package com.lemon.testcases;/*
author:carol
**/

import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lemon.base.BaseCase;
import com.lemon.pojo.CaseInfo;
import io.qameta.allure.Allure;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static io.restassured.RestAssured.given;

public class GetUserInfoTestv2 extends BaseCase {
    List<CaseInfo> caseInfoList;
    @BeforeClass
    public void setup(){

        caseInfoList = getCaseDataFromExcel(3);
        caseInfoList = paramReplace(caseInfoList);
    }


    @Test(dataProvider = "getUserInfoDatas")
    public void testUserInfo(CaseInfo caseInfo) throws JsonProcessingException, FileNotFoundException {
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
                    headers(headerMap).
                when().
                    get(caseInfo.getUrl()).
                then().log().body().

                extract().response();
        InputStream inputStream = new FileInputStream(logToFile);
        Allure.addAttachment("请求接口响应信息",inputStream);
//        ObjectMapper mapper2 = new ObjectMapper();
//        Map expectedMap = mapper2.readValue(caseInfo.getExpected(), Map.class);
//        Set<Map.Entry<String,Object>> set = expectedMap.entrySet();
//        for (Map.Entry<String, Object> map : set) {
////           关键点：做断言。通过Gpath获取实际接口响应对应字段的值
////            我们在Excel里面写用例的期望结果时，期望结果里面键名--->Gpath表达式
////            期望结果里面键值--->期望值
//            System.out.println(map.getKey());
//            Assert.assertEquals(res.path(map.getKey()),map.getValue());
//        }
        assertExpected(caseInfo,res);


    }

    @DataProvider
    public Object[] getUserInfoDatas(){

        return caseInfoList.toArray();
    }


}
