package com.lemon.baseCase;

import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lemon.Datas.Constants;
import com.lemon.Datas.GlobalEnvironment;
import com.lemon.pojo.CaseInfo;
import com.lemon.utils.JDBCUtils;
import io.restassured.RestAssured;
import io.restassured.config.LogConfig;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.path.json.config.JsonPathConfig;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;

import java.io.DataOutput;
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

/**
 * @Project: api_auto_test
 * @Site: http://www.lemonban.com
 * @Forum: http://testingpai.com
 * @Copyright: 2020 版权所有 湖南省零檬信息技术有限公司
 * @Author: Carol
 * @Create: 2020-12-23 10:21
 * @Desc：
 **/

public class BaseCase {
    @BeforeTest
    public void globalSetup() throws FileNotFoundException {
//        整体全局性前置配置/初始化

//        1.设置项目的BaseURL
        RestAssured.baseURI = "http://api.lemonban.com/futureloan";
//        2.设置接口响应结果，如果是json返回的小数类型，使用BigDecimal类型来存储

        RestAssured.config = RestAssured.config().jsonConfig(jsonConfig().numberReturnType(JsonPathConfig.NumberReturnType.BIG_DECIMAL));
//      3. 设置项目的日志存储到本地文件中,将所有的log都输入到该文件中
//        此时会输入到log文件中，控制台也会有打印

//        PrintStream fileOutPutStream = new PrintStream(new File("log/test_all.log"));
//        RestAssured.filters(new RequestLoggingFilter(fileOutPutStream),new ResponseLoggingFilter(fileOutPutStream));


    }

    List<CaseInfo> caseInfoList;

    public String regexReplace(String oldStr){
        if(oldStr == null){
            return oldStr;
        }
//        参数化替换功能替换
//        正则表达式：
//        "."  匹配任意的字符
//        "*"  匹配前面的字符零次或者任意次数
//        "?"  贪婪匹配
//        .*?
//          1.定义正则表达式
        String regex = "\\{\\{(.*?)\\}\\}";
//        2.通过正则表达式编译一个匹配器pattern
        Pattern pattern = Pattern.compile(regex);
//        3.开始进行匹配 参数：为你要去在哪一个字符串里面去进行匹配
        Matcher matcher = pattern.matcher(oldStr);
//        4.连续查找，连续匹配
        String findStr = "";
        String singleStr ="";
        while(matcher.find()){
            findStr = matcher.group(0);
//            System.out.println(matcher.group(1));
            singleStr = matcher.group(1);
            Object replaceStr = GlobalEnvironment.env.get(singleStr);
            oldStr = oldStr.replace(findStr,replaceStr+"");
        }
        return oldStr;

    }

    public List<CaseInfo> paramReplace(List<CaseInfo> caseInfoList){
        for (CaseInfo caseInfo : caseInfoList) {

                String getRequestHeader = regexReplace(caseInfo.getRequestHeader());
                caseInfo.setRequestHeader(getRequestHeader);

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
    public CaseInfo paramReplaceCaseInfo(CaseInfo caseInfo){


            String getRequestHeader = regexReplace(caseInfo.getRequestHeader());
            caseInfo.setRequestHeader(getRequestHeader);

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
    public List<CaseInfo> getCaseDataFromExcel(int index) {
        ImportParams importParams = new ImportParams();
//        sheet索引，默认起始值为0
        importParams.setStartSheetIndex(index);
////        要杜读取的sheet数量
//        importParams.setSheetNum(2);
        File excelFile = new File("src/test/resources/testcasehome2.xls");
        List<CaseInfo> list = ExcelImportUtil.importExcel(excelFile, CaseInfo.class, importParams);
        return list;
    }
    public void assertExpected(CaseInfo caseInfo, Response res){
        Map expectedMap = fromJsonToMap(caseInfo.getExpected());
        Set<Map.Entry<String,Object>> set = expectedMap.entrySet();
        for (Map.Entry<String, Object> map : set) {
//           关键点：做断言。通过Gpath获取实际接口响应对应字段的值
//            我们在Excel里面写用例的期望结果时，期望结果里面键名--->Gpath表达式
//            期望结果里面键值--->期望值
            Object expected = map.getValue();
            if (expected instanceof Float || expected instanceof Double){
                BigDecimal bigDecimal = new BigDecimal(expected.toString());
                Assert.assertEquals(res.path(map.getKey()),bigDecimal);
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
    public String addLogDir(String dirName,int caseId){

            String logFilePath = "";
            if (!Constants.IS_DEBUG) {
                //提前创建好目录层级
                String dirPath = "target/log/" + dirName;
                File dirFile = new File(dirPath);
                if (!dirFile.isDirectory()) {
//            创建目录
                    dirFile.mkdirs();

                }
//        请求之前对日志做配置，输出到对应文件中
//          把log保存到文件中去，每一次都新建一个log文件，只会在log文件中有log输出，控制台不会有打印
                logFilePath = "target/log/" + dirName + "/" + dirName + "_" + caseId + ".log";
                PrintStream fileOutPutStream = null;
                try {
                    fileOutPutStream = new PrintStream(new File(logFilePath));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                RestAssured.config = RestAssured.config().logConfig(LogConfig.logConfig().defaultStream(fileOutPutStream));

            }
        return logFilePath;

    }
}
