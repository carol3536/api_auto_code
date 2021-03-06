package com.test.day03;/*
author:carol
**/

import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static io.restassured.RestAssured.given;

public class GetUserInfoTest {
    List<CaseInfo> caseInfoList;
    @BeforeTest
    public void setup(){

        caseInfoList = getCaseDataFromExcel(2);
    }


    @Test(dataProvider = "getUserInfoDatas")
    public void testUserInfo(CaseInfo caseInfo) throws JsonProcessingException {
//        参数化替换
//        1.接口URL地址{{memberId}}给替换成环境变量中保存的值
        String url = regexReplace(caseInfo.getUrl(), GlobalEnvironment.memberId + "");
//        2.响应结果中{{memberId}}给替换成环境变量中保存的值
        ObjectMapper objectMapper = new ObjectMapper();
        String expected = regexReplace(caseInfo.getExpected(),GlobalEnvironment.memberId+"");
        Map headerMap = objectMapper.readValue(caseInfo.getRequestHeader(), Map.class);
        System.out.println(headerMap);

        Response res =
                given().
                    headers(headerMap).
                when().
                    get("http://api.lemonban.com/futureloan" + url).
                then().

                extract().response();
        ObjectMapper mapper2 = new ObjectMapper();
        Map expectedMap = mapper2.readValue(expected, Map.class);
        Set<Map.Entry<String,Object>> set = expectedMap.entrySet();
        for (Map.Entry<String, Object> map : set) {
//           关键点：做断言。通过Gpath获取实际接口响应对应字段的值
//            我们在Excel里面写用例的期望结果时，期望结果里面键名--->Gpath表达式
//            期望结果里面键值--->期望值
            Assert.assertEquals(res.path(map.getKey()),map.getValue());
        }

    }

    @DataProvider
    public Object[] getUserInfoDatas(){

        return caseInfoList.toArray();
    }
    public List<CaseInfo> getCaseDataFromExcel(int index) {
        ImportParams importParams = new ImportParams();
//        sheet索引，默认起始值为0
        importParams.setStartSheetIndex(index);
////        要杜读取的sheet数量
//        importParams.setSheetNum(2);
        File excelFile = new File("src/test/resources/testcasehome1.xls");
        List<CaseInfo> list = ExcelImportUtil.importExcel(excelFile, CaseInfo.class, importParams);
        return list;
    }
    public String regexReplace(String oldStr,String newStr){
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
        while(matcher.find()){
             findStr = matcher.group(0);
//            System.out.println(matcher.group(1));
        }
        return oldStr.replace(findStr,newStr);
    }

    public static void main(String[] args) {
        Integer memberId = 123344;
        String str1 = "/member/{{memberId}}/info";
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
        Matcher matcher = pattern.matcher(str1);
//        4.连续查找，连续匹配
        String findStr = "";
        while(matcher.find()){
//            System.out.println(matcher.group(0));
//            System.out.println(matcher.group(1));
              findStr = matcher.group(0);
        }
        String replaceStr = str1.replace(findStr, "1122334455");
        System.out.println(replaceStr);


    }
}
