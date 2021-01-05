package com.lemon.testcase;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.lemon.baseCase.BaseCase;
import com.lemon.pojo.CaseInfo;
import com.test.day03.GlobalEnvironment;
import io.qameta.allure.Allure;
import io.restassured.response.Response;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;

/**
 * @Project: api_auto_test2
 * @Site: http://www.lemonban.com
 * @Forum: http://testingpai.com
 * @Copyright: 2020 版权所有 湖南省零檬信息技术有限公司
 * @Author: Carol
 * @Create: 2021-01-04 20:06
 * @Desc：
 **/

public class AuditProject extends BaseCase {
    List<CaseInfo> caseInfoList;
    @BeforeClass
    public void setup(){

        caseInfoList = getCaseDataFromExcel(6);
        caseInfoList = paramReplace(caseInfoList);
    }


    @Test(dataProvider = "getAuditDatas")
    public void testAddLoan(CaseInfo caseInfo) throws JsonProcessingException, FileNotFoundException {
        Map headerMap = fromJsonToMap(caseInfo.getRequestHeader());
        System.out.println(headerMap);
        String logDir = addLogDir(caseInfo.getInterfaceName(), caseInfo.getCaseId());
        Response res =
                given().log().all().
                        //让REST-Assured返回json小数的时候，使用BigDecimal类型来存储小数（默认时Float存储的）
//                        config(RestAssured.config().jsonConfig(jsonConfig().numberReturnType(JsonPathConfig.NumberReturnType.BIG_DECIMAL))).
        headers(headerMap).
                        body(caseInfo.getInputParams()).
                        when().
                        patch( caseInfo.getUrl()).
                        then().log().body().

                        extract().response();
        InputStream inputStream = new FileInputStream(logDir);
        Allure.addAttachment("接口请求响应信息",inputStream);

        assertExpected(caseInfo,res);


    }

    @DataProvider
    public Object[] getAuditDatas(){

        return caseInfoList.toArray();
    }


}
