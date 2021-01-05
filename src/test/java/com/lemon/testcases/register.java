package com.lemon.testcases;/*
author:carol
**/

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lemon.base.BaseCase;
import com.lemon.data.GlobalEnvironment;
import com.lemon.pojo.CaseInfo;
import com.lemon.util.PhoneRandom;
import io.qameta.allure.Allure;
import io.restassured.RestAssured;
import io.restassured.config.LogConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static io.restassured.RestAssured.given;

public class register extends BaseCase {
    List<CaseInfo> caseInfoList;

    @BeforeClass
    public void setup(){
        caseInfoList = getCaseDataFromExcel(0);

    }
    @Test(dataProvider = "getRegisterDatas")
    public void testRegister(CaseInfo caseInfo) throws JsonProcessingException, FileNotFoundException {
        if (caseInfo.getCaseId() == 1){
            String phoneRandom1 = PhoneRandom.getPhoneRandom();
            GlobalEnvironment.envDatas.put("mobile_phone1", phoneRandom1);
        }else if (caseInfo.getCaseId() == 2){
            String phoneRandom2 = PhoneRandom.getPhoneRandom();
            GlobalEnvironment.envDatas.put("mobile_phone2", phoneRandom2);
        }else if (caseInfo.getCaseId() == 3){
            String phoneRandom3 = PhoneRandom.getPhoneRandom();
            GlobalEnvironment.envDatas.put("mobile_phone3", phoneRandom3);
        }else if (caseInfo.getCaseId() == 4){
            String phoneRandom4 = PhoneRandom.getPhoneRandom();
            GlobalEnvironment.envDatas.put("mobile_phone4", phoneRandom4);
        }else {
            String phoneRandom5 = PhoneRandom.getPhoneRandom();
            GlobalEnvironment.envDatas.put("mobile_phone5", phoneRandom5);
        }
            caseInfo = getParamReplace(caseInfo);
            //        String jsonStr = "{\"mobile_phone\": \"15515341409\", \"pwd\": \"lemon66622\"}";
//        字符串请求行转换为Map
//        实现思路：把原有的字符串转换为json数据类型保存，通过ObjectMapper来转换为Map
//        Jackson json字符串--》map
//        1.实例化ObjectMapper对象
//        ObjectMapper objectMapper = new ObjectMapper();
//        readValue方法参数解释
//        第一个参数：json字符串  第二个参数：转成的类型（map）
//        Map headerMap = objectMapper.readValue(caseInfo.getRequestHeader(), Map.class);

        Map headerMap = fromJsonToMap(caseInfo.getRequestHeader());
        System.out.println(headerMap);
//        String dirPath = "logInfo/log/register";
//        File dirFile = new File(dirPath);
//        if (!dirFile.isDirectory()){
//            dirFile.mkdirs();
//        }
////        请求之前对日志做配置，输出到对应文件中
//        PrintStream fileOutPutStream = new PrintStream(new File("logInfo/log/register/register_"+caseInfo.getCaseId()+".log"));
//        RestAssured.config = RestAssuredConfig.config().logConfig(LogConfig.logConfig().defaultStream(fileOutPutStream));
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
        assertSqlExpected(caseInfo);
        //在登录模块用例执行结束之后将memberId保存到环境变量中
//        1。拿到正常用例返回响应信息里面的memberId
        Integer memberId =  res.path("data.id");

//        分三个角色时，需要的数据操作
//        ObjectMapper objectMapper1 = new ObjectMapper();
//        Map inputMap = objectMapper1.readValue(caseInfo.getInputParams(), Map.class);
       Map inputMap = fromJsonToMap(caseInfo.getInputParams());
        Object pwd = inputMap.get("pwd");
        if (caseInfo.getCaseId() == 1){
            GlobalEnvironment.envDatas.put("memberId1",memberId);
            GlobalEnvironment.envDatas.put("mobile_phone1",res.path("data.mobile_phone"));
            GlobalEnvironment.envDatas.put("pwd1", pwd+"");
        }else if (caseInfo.getCaseId() == 2){
            GlobalEnvironment.envDatas.put("memberId2",memberId);
            GlobalEnvironment.envDatas.put("mobile_phone2",res.path("data.mobile_phone"));
            GlobalEnvironment.envDatas.put("pwd2", pwd+"");
        }else if (caseInfo.getCaseId() == 3){
            GlobalEnvironment.envDatas.put("memberId3",memberId);
            GlobalEnvironment.envDatas.put("mobile_phone3",res.path("data.mobile_phone"));
            GlobalEnvironment.envDatas.put("pwd3", pwd+"");
        }


//        if(memberId != null){
//            //        2.保存到环境变量中
////            GlobalEnvironment.memberId = memberId;
//            String mobile_phone = res.path("data.mobile_phone");
//            System.out.println(mobile_phone);
//            GlobalEnvironment.envDatas.put("memberId",memberId);
//            GlobalEnvironment.envDatas.put("mobile_phone",mobile_phone);
//            ObjectMapper objectMapper1 = new ObjectMapper();
//            Map inputMap = objectMapper1.readValue(caseInfo.getInputParams(), Map.class);
//            Object pwd = inputMap.get("pwd");
//            GlobalEnvironment.envDatas.put("pwd", pwd+"");
//            System.out.println(GlobalEnvironment.envDatas.get("pwd"));
//        }
    }

    @DataProvider
    public Object[] getRegisterDatas(){

        return caseInfoList.toArray();
    }

    public static void main(String[] args) {
        String dirPath = "test/log";
        File dirFile = new File(dirPath);
        if (!dirFile.isDirectory()){
            dirFile.mkdirs();
        }
    }
}
