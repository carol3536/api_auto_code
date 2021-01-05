package com.lemon.testcases;/*
author:carol
**/

import com.fasterxml.jackson.core.JsonProcessingException;
import com.lemon.base.BaseCase;
import com.lemon.pojo.CaseInfo;
import io.qameta.allure.Allure;
import io.restassured.RestAssured;
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

import static io.restassured.RestAssured.given;
import static io.restassured.config.JsonConfig.jsonConfig;

public class AddLoan extends BaseCase {
    List<CaseInfo> caseInfoList;
    @BeforeClass
    public void setup(){

        caseInfoList = getCaseDataFromExcel(5);
        caseInfoList = paramReplace(caseInfoList);
    }


    @Test(dataProvider = "getAddLoanDatas")
    public void testAddLoan(CaseInfo caseInfo) throws JsonProcessingException, FileNotFoundException {
//
        Map headerMap = fromJsonToMap(caseInfo.getRequestHeader());
        System.out.println(headerMap);
        String logToFile = addLogToFile(caseInfo.getInterfaceName(), caseInfo.getCaseId());
        Response res =
                given().
config(RestAssured.config().jsonConfig(jsonConfig().numberReturnType(JsonPathConfig.NumberReturnType.BIG_DECIMAL))).
                        headers(headerMap).
                        body(caseInfo.getInputParams()).
                        when().
                        post(caseInfo.getUrl()).

                        then().
                        log().body().

                        extract().response();
        InputStream inputStream = new FileInputStream(logToFile);
        Allure.addAttachment("请求接口响应信息",inputStream);
        assertExpected(caseInfo,res);
        assertSqlExpected(caseInfo);

    }

    @DataProvider
    public Object[] getAddLoanDatas(){

        return caseInfoList.toArray();
    }

}
