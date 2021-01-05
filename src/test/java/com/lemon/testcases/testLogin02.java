package com.lemon.testcases;


import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lemon.base.BaseCase;
import com.lemon.data.GlobalEnvironment;
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

import static io.restassured.RestAssured.given;

/**
 * @Project: api_auto_test
 * @Site: http://www.lemonban.com
 * @Forum: http://testingpai.com
 * @Copyright: 2020 版权所有 湖南省零檬信息技术有限公司
 * @Author: Carol
 * @Create: 2020-12-18 16:06
 * @Desc：
 **/

public class testLogin02 extends BaseCase{
    List<CaseInfo> caseInfoList;
    @BeforeClass
    public void setup(){
        caseInfoList = getCaseDataFromExcel(1);
        caseInfoList = paramReplace(caseInfoList);
    }
    @Test(dataProvider = "getLoginDatas")
    public void testLogin(CaseInfo caseInfo) throws JsonProcessingException, FileNotFoundException {
//        String jsonStr = "{\"mobile_phone\": \"15515341409\", \"pwd\": \"lemon66622\"}";
//        字符串请求行转换为Map
//        实现思路：把原有的字符串转换为json数据类型保存，通过ObjectMapper来转换为Map
//        Jackson json字符串--》map
////        1.实例化ObjectMapper对象
//        ObjectMapper objectMapper = new ObjectMapper();
////        readValue方法参数解释
////        第一个参数：json字符串  第二个参数：转成的类型（map）
//        Map headerMap = objectMapper.readValue(caseInfo.getRequestHeader(), Map.class);
        Map headerMap = fromJsonToMap(caseInfo.getRequestHeader());
        System.out.println(headerMap);
        System.out.println(caseInfo.getInputParams());
        String logToFile = addLogToFile(caseInfo.getInterfaceName(), caseInfo.getCaseId());
        Response res = given().
                headers(headerMap).
                body(caseInfo.getInputParams()).

        when().
                post(caseInfo.getUrl()).
        then().log().body().

                extract().response();
        InputStream inputStream = new FileInputStream(logToFile);
        Allure.addAttachment("请求接口响应信息",inputStream);
//        String expected = caseInfo.getExpected();
//        ObjectMapper mapper2 = new ObjectMapper();
//        Map expectedMap = mapper2.readValue(expected, Map.class);
//        Set<Map.Entry<String,Object>> set = expectedMap.entrySet();
//        for (Map.Entry<String, Object> map : set) {
////           关键点：做断言。通过Gpath获取实际接口响应对应字段的值
////            我们在Excel里面写用例的期望结果时，期望结果里面键名--->Gpath表达式
////            期望结果里面键值--->期望值
//            Assert.assertEquals(res.path(map.getKey()),map.getValue());
//        }
        assertExpected(caseInfo,res);


        //在登录模块用例执行结束之后将memberId保存到环境变量中
//        1。拿到正常用例返回响应信息里面的memberId
        Integer memberId =  res.path("data.id");
        if(memberId != null){
            //        2.保存到环境变量中
//            GlobalEnvironment.memberId = memberId;
            if (caseInfo.getCaseId() == 1){
                GlobalEnvironment.envDatas.put("token1",res.path("data.token_info.token"));
            }else if (caseInfo.getCaseId() == 2){
                GlobalEnvironment.envDatas.put("token2",res.path("data.token_info.token"));
            }else if (caseInfo.getCaseId() == 3){
                GlobalEnvironment.envDatas.put("token3",res.path("data.token_info.token"));
            }

        }
    }

    @DataProvider
    public Object[] getLoginDatas(){

        return caseInfoList.toArray();
    }
}
