package com.lemon.testcase;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lemon.baseCase.BaseCase;
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
import java.util.Set;

import static io.restassured.RestAssured.given;
import static io.restassured.config.JsonConfig.jsonConfig;

/**
 * @Project: api_auto_test
 * @Site: http://www.lemonban.com
 * @Forum: http://testingpai.com
 * @Copyright: 2020 版权所有 湖南省零檬信息技术有限公司
 * @Author: Carol
 * @Create: 2020-12-28 14:01
 * @Desc：
 **/

public class RechargeTest extends BaseCase {
    List<CaseInfo> caseInfoList;
    @BeforeClass
    public void setup(){

        caseInfoList = getCaseDataFromExcel(4);
        caseInfoList = paramReplace(caseInfoList);
    }


    @Test(dataProvider = "getRechargeDatas")
    public void rechargeTest(CaseInfo caseInfo) throws JsonProcessingException, FileNotFoundException {


         Map headerMap = fromJsonToMap(caseInfo.getRequestHeader());
        System.out.println(headerMap);

        String logDir = addLogDir(caseInfo.getInterfaceName(), caseInfo.getCaseId());
        Response res =
                given().log().all().
                        //                        让REST-Assured返回json小数的时候，使用BigDecimal类型来存储小数（默认时Float存储的）

                        headers(headerMap).
                        body(caseInfo.getInputParams()).
                        when().
                        post( caseInfo.getUrl()).
                        then().log().body().

                        extract().response();

        InputStream inputStream = new FileInputStream(logDir);
        Allure.addAttachment("接口请求响应信息",inputStream);
//响应结果断言
       assertExpected(caseInfo,res);
//       数据库断言
       assertSqlExpected(caseInfo);

    }

    @DataProvider
    public Object[] getRechargeDatas(){

        return caseInfoList.toArray();
    }


}
