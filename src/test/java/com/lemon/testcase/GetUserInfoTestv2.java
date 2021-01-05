package com.lemon.testcase;/*
author:carol
**/

import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lemon.baseCase.BaseCase;
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

        Map headerMap = fromJsonToMap(caseInfo.getRequestHeader());
        System.out.println(headerMap);
        String logDir = addLogDir(caseInfo.getInterfaceName(), caseInfo.getCaseId());
        Response res =
                given().log().all().
                    headers(headerMap).
                when().
                    get(caseInfo.getUrl()).
                then().log().all().

                extract().response();

        InputStream inputStream = new FileInputStream(logDir);
        Allure.addAttachment("接口请求响应信息",inputStream);
//        ---------------------封装了断言部分
        assertExpected(caseInfo,res);


    }

    @DataProvider
    public Object[] getUserInfoDatas(){

        return caseInfoList.toArray();
    }


}
