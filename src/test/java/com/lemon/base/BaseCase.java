package com.lemon.base;/*
author:carol
**/

import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lemon.data.Constants;
import com.lemon.data.GlobalEnvironment;
import com.lemon.pojo.CaseInfo;
import com.lemon.util.JDBCUtils;
import io.restassured.RestAssured;
import io.restassured.config.LogConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.path.json.config.JsonPathConfig;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static io.restassured.config.JsonConfig.jsonConfig;

public class BaseCase extends Constants {

    @BeforeTest
    public void globalSetup() throws FileNotFoundException {
//        整体全局性前置配置/初始化

//        1.设置项目的BaseURL
        RestAssured.baseURI = "http://api.lemonban.com/futureloan";
//        2.设置接口响应结果，如果是json返回的小数类型，使用BigDecimal类型来存储
        RestAssured.config = RestAssured.config().jsonConfig(jsonConfig().numberReturnType(JsonPathConfig.NumberReturnType.BIG_DECIMAL));
//      3.设置项目的日志存储到本地文件中
        PrintStream fileOutPutStream = new PrintStream(new File("log/test_all.log"));
        RestAssured.filters(new RequestLoggingFilter(fileOutPutStream),new ResponseLoggingFilter(fileOutPutStream));

    }
    List<CaseInfo> caseInfoList;
    public List<CaseInfo> getCaseDataFromExcel(int index) {
        ImportParams importParams = new ImportParams();
//        sheet索引，默认起始值为0
        importParams.setStartSheetIndex(index);
////        要杜读取的sheet数量
//        importParams.setSheetNum(2);
        File excelFile = new File(excelPath);
        List<CaseInfo> list = ExcelImportUtil.importExcel(excelFile, CaseInfo.class, importParams);
        return list;
    }
    public String regexReplace(String oldStr){
//        参数化替换功能替换
//        正则表达式：
//        "."  匹配任意的字符
//        "*"  匹配前面的字符零次或者任意次数
//        "?"  贪婪匹配
//        .*?
//          1.定义正则表达式
        if(oldStr == null){
            return oldStr;
        }
        String regex = "\\{\\{(.*?)\\}\\}";
//        2.通过正则表达式编译一个匹配器pattern
        Pattern pattern = Pattern.compile(regex);
//        3.开始进行匹配 参数：为你要去在哪一个字符串里面去进行匹配
        Matcher matcher = pattern.matcher(oldStr);
//        4.连续查找，连续匹配
        String findStr = "";
        String singleStr = "";
        while(matcher.find()){
            findStr = matcher.group(0);
            System.out.println(findStr);
            System.out.println(matcher.group(1));
            singleStr = matcher.group(1);
            Object  replaceStr =  GlobalEnvironment.envDatas.get(singleStr);
            oldStr = oldStr.replace(findStr,replaceStr+"");
        }

      return oldStr;
    }
    public List<CaseInfo> paramReplace(List<CaseInfo> caseInfoList){
//        参数化处理：请求头，接口地址，参数输入，期望返回结果
        for (CaseInfo caseInfo : caseInfoList) {

                String requestHeader = regexReplace(caseInfo.getRequestHeader());
                caseInfo.setRequestHeader(requestHeader);


                String getUrl = regexReplace(caseInfo.getUrl());
                caseInfo.setUrl(getUrl);


                String getInputParams = regexReplace(caseInfo.getInputParams());
                caseInfo.setInputParams(getInputParams);

                String getExpected = regexReplace(caseInfo.getExpected());
                caseInfo.setExpected(getExpected);

            String getCheckSql = regexReplace(caseInfo.getCheckSql());
            caseInfo.setCheckSql(getCheckSql);

        }
        return caseInfoList;
    }
    public CaseInfo  getParamReplace(CaseInfo caseInfo){
//        参数化处理：请求头，接口地址，参数输入，期望返回结果


            String requestHeader = regexReplace(caseInfo.getRequestHeader());
            caseInfo.setRequestHeader(requestHeader);


            String getUrl = regexReplace(caseInfo.getUrl());
            caseInfo.setUrl(getUrl);


            String getInputParams = regexReplace(caseInfo.getInputParams());
            caseInfo.setInputParams(getInputParams);

            String getExpected = regexReplace(caseInfo.getExpected());
            caseInfo.setExpected(getExpected);

            String getCheckSql = regexReplace(caseInfo.getCheckSql());
            caseInfo.setCheckSql(getCheckSql);

        return caseInfo;
    }
    /**
     * 用例公共的断言方法，断言期望值和实际值
     * @param caseInfo 用例信息
     * @param res 接口的响应结果
     */
    public void assertExpected(CaseInfo caseInfo, Response res){
//        //断言
//        //1、把断言数据由json字符串转换为map
//        ObjectMapper objectMapper2 = new ObjectMapper();
//        Map expectedMap = null;
//        try {
//            expectedMap = objectMapper2.readValue(caseInfo.getExpected(), Map.class);
//        } catch (JsonProcessingException e) {
//            e.printStackTrace();
//        }
//        //2、循环遍历取到map里面每一组键值对
//        Set<Map.Entry<String, Object>> set = expectedMap.entrySet();
//        for (Map.Entry<String, Object> map : set){
//            Object expected = map.getValue();
//            if((expected instanceof Float) || (expected instanceof Double)){
//                //把期望值转换(期望的json结果是小数类型-Float/Double才需要转换)
//                //map.getValue() 判断一下，是不是小数类型（Float/Double）
//                //把Float类型转换为BigDecimal类型
//                BigDecimal bigDecimalData = new BigDecimal(map.getValue().toString());
//                Assert.assertEquals(res.path(map.getKey()),bigDecimalData);
//            }else {
//                Assert.assertEquals(res.path(map.getKey()),expected);
//            }
//        }
//
        ObjectMapper mapper2 = new ObjectMapper();
        Map expectedMap = null;
        try {
            expectedMap = mapper2.readValue( caseInfo.getExpected(), Map.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        Set<Map.Entry<String,Object>> set = expectedMap.entrySet();
        for (Map.Entry<String, Object> map : set) {
//           关键点：做断言。通过Gpath获取实际接口响应对应字段的值
//            我们在Excel里面写用例的期望结果时，期望结果里面键名--->Gpath表达式
//            期望结果里面键值--->期望值
//            System.out.println("期望"+map.getValue().getClass());
//            System.out.println( "实际"+res.path(map.getKey()).getClass());
            Object expected = map.getValue();

            if (expected instanceof Float || expected instanceof Double){
                //把期望值转换(期望的json结果是小数类型-Float/Double才需要转换)
//                //map.getValue() 判断一下，是不是小数类型（Float/Double）
//                //把Float类型转换为BigDecimal类型
                BigDecimal bigDecimalData = new BigDecimal(expected.toString());
                Assert.assertEquals(res.path(map.getKey()),bigDecimalData);
            }else {
                Assert.assertEquals(res.path(map.getKey()),expected);
            }
        }
    }
    public void assertSqlExpected(CaseInfo caseInfo){
        if (caseInfo.getCheckSql() != null) {
            Map checkSqlMap = fromJsonToMap(caseInfo.getCheckSql());
            Set<Map.Entry<String, Object>> set = checkSqlMap.entrySet();
            for (Map.Entry<String, Object> map : set) {

                Object expected = map.getValue();
                Object actual = JDBCUtils.querySingle(map.getKey());
                System.out.print("------------------expected ");
                System.out.println(expected.getClass());
                System.out.print("------------------actual ");
                System.out.println(actual.getClass());
                if (actual instanceof Long) {
                    Long expectedLong = new Long(expected.toString());
                    Assert.assertEquals(actual, expectedLong,"数据库断言失败");
                    System.out.println("Long和Integer类型断言");
                } else if (actual instanceof BigDecimal){
                    BigDecimal expectedBigdecimal = new BigDecimal(expected.toString());
                    Assert.assertEquals(actual,expectedBigdecimal,"断言失败");
                    System.out.println("Double and BigDecimal type assert");
                } else {
                    Assert.assertEquals(actual, expected,"sql assert fail");
                    System.out.println("String type assert");
                }
            }
        }

    }
    /*
     * 把json字符串转成map类型
     * @param jsonStr json字符串
     * */
    public Map fromJsonToMap(String jsonStr){
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return  objectMapper.readValue(jsonStr, Map.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }
public String addLogToFile(String interfaceName,int caseId) {
        String logFile = "";
    if (!IS_DEBUG) {
        String dirPath = "logInfo/log/" + interfaceName;
        File dirFile = new File(dirPath);
        if (!dirFile.isDirectory()) {
            dirFile.mkdirs();
        }
        logFile = "logInfo/log/" + interfaceName + "/" + interfaceName + "_" + caseId + ".log";
//        请求之前对日志做配置，输出到对应文件中
        PrintStream fileOutPutStream = null;
        try {
            fileOutPutStream = new PrintStream(new File(logFile));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        RestAssured.config = RestAssuredConfig.config().logConfig(LogConfig.logConfig().defaultStream(fileOutPutStream));
    }
    return logFile;
}
}
