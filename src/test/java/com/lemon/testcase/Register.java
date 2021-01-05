package com.lemon.testcase;

import cn.afterturn.easypoi.excel.entity.ImportParams;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lemon.Datas.GlobalEnvironment;
import com.lemon.baseCase.BaseCase;
import com.lemon.pojo.CaseInfo;
import com.lemon.utils.ParamRandom;
import io.qameta.allure.Allure;
import io.restassured.RestAssured;
import io.restassured.config.LogConfig;
import io.restassured.response.Response;
import jdk.internal.util.xml.impl.Input;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.sessionId;

/**
 * @Project: api_auto_test
 * @Site: http://www.lemonban.com
 * @Forum: http://testingpai.com
 * @Copyright: 2020 版权所有 湖南省零檬信息技术有限公司
 * @Author: Carol
 * @Create: 2020-12-23 17:21
 * @Desc：
 **/

public class Register extends BaseCase {
    List<CaseInfo> caseInfoList;
    @BeforeClass
    public  void setup(){
        caseInfoList = getCaseDataFromExcel(0);
    }


    @Test(dataProvider = "getRegisterDatas")
    public void testRegister(CaseInfo caseInfo) throws JsonProcessingException, FileNotFoundException {
//        RestAssured.baseURI = "http://api.lemonban.com/futureloan";
        if (caseInfo.getCaseId() == 1) {
            String paramRandomNum1 = ParamRandom.getParamRandom();
            System.out.println(paramRandomNum1);
            GlobalEnvironment.env.put("mobile_phone1", paramRandomNum1);

        } else if (caseInfo.getCaseId() == 2) {
            String paramRandomNum2 = ParamRandom.getParamRandom();
            GlobalEnvironment.env.put("mobile_phone2", paramRandomNum2);
        } else if (caseInfo.getCaseId() == 3) {
            String paramRandomNum3 = ParamRandom.getParamRandom();
            GlobalEnvironment.env.put("mobile_phone3", paramRandomNum3);
        } else if (caseInfo.getCaseId() == 4) {
            String paramRandomNum4 = ParamRandom.getParamRandom();
            GlobalEnvironment.env.put("mobile_phone4", paramRandomNum4);
        } else if (caseInfo.getCaseId() == 9) {
            String paramRandomNum5 = ParamRandom.getParamRandom();
            GlobalEnvironment.env.put("mobile_phone5", paramRandomNum5);
        }
        paramReplaceCaseInfo(caseInfo);
//        1.实例化ObjectMapper对象
//        ObjectMapper objectMapper = new ObjectMapper();
////        readValue方法参数解释
////        第一个参数：json字符串  第二个参数：转成的类型（map）
//        Map headerMap = objectMapper.readValue(caseInfo.getRequestHeader(), Map.class);
        Map headerMap = fromJsonToMap(caseInfo.getRequestHeader());
        System.out.println(headerMap);
////提前创建好目录层级
//        String dirPath = "target/log/register";
//        File dirFile = new File(dirPath);
//        if (!dirFile.isDirectory()){
////            创建目录
//            dirFile.mkdirs();
//        }
////        请求之前对日志做配置，输出到对应文件中
////          把log保存到文件中去，每一次都新建一个log文件
//        PrintStream fileOutPutStream = new PrintStream(new File("target/log/register/register_"+caseInfo.getCaseId()+".log"));
//        RestAssured.config = RestAssured.config().logConfig(LogConfig.logConfig().defaultStream(fileOutPutStream));
        String logDir = addLogDir(caseInfo.getInterfaceName(), caseInfo.getCaseId());
        Response res = given().log().all().
                headers(headerMap).
                body(caseInfo.getInputParams()).

                when().
                post(caseInfo.getUrl()).
                then().log().all().

                extract().response();
//        String expected = caseInfo.getExpected();
//        ObjectMapper mapper2 = new ObjectMapper();
//        Map expectedMap = mapper2.readValue(expected, Map.class);
//        Set<Map.Entry<String, Object>> set = expectedMap.entrySet();
//        for (Map.Entry<String, Object> map : set) {
////           关键点：做断言。通过Gpath获取实际接口响应对应字段的值
////            我们在Excel里面写用例的期望结果时，期望结果里面键名--->Gpath表达式
////            期望结果里面键值--->期望值
//            Assert.assertEquals(res.path(map.getKey()), map.getValue());
////        }
        InputStream inputStream = new FileInputStream(logDir);
        Allure.addAttachment("接口请求响应信息",inputStream);




//        ---------------------响应结果断言，封装了断言部分
        assertExpected(caseInfo,res);
//        数据库断言
        assertSqlExpected(caseInfo);

        //在登录模块用例执行结束之后将memberId保存到环境变量中
//        1。拿到正常用例返回响应信息里面的memberId
       Map inputParams =  fromJsonToMap(caseInfo.getInputParams());
        Object pwd = inputParams.get("pwd");
        Object memberId = res.path("data.id");
        if (memberId != null) {
            if (caseInfo.getCaseId() == 1) {
                GlobalEnvironment.env.put("memberId1", memberId);

                GlobalEnvironment.env.put("pwd1", pwd + "");

            } else if (caseInfo.getCaseId() == 2) {
                GlobalEnvironment.env.put("memberId2", memberId);

                GlobalEnvironment.env.put("pwd2", pwd + "");
            } else if (caseInfo.getCaseId() == 3) {
                GlobalEnvironment.env.put("memberId3", memberId);

                GlobalEnvironment.env.put("pwd3", pwd + "");
            }


        }
    }

    @DataProvider
    public Object[] getRegisterDatas(){

        return caseInfoList.toArray();
    }

//    public static void main(String[] args) {
//        String dirPath = "testLog/log";
//        File dirFile = new File(dirPath);
//        if (!dirFile.isDirectory()){
////            创建目录
//            dirFile.mkdirs();
//        }
//    }

}
