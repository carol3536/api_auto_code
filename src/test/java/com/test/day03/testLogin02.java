package com.test.day03;


import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.File;
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

public class testLogin02 {
    List<CaseInfo> caseInfoList;
    @BeforeClass
    public void setup(){
        caseInfoList = getCaseDataFromExcel(1);
    }
    @Test(dataProvider = "getLoginDatas")
    public void testLogin(CaseInfo caseInfo) throws JsonProcessingException {
//        String jsonStr = "{\"mobile_phone\": \"15515341409\", \"pwd\": \"lemon66622\"}";
//        字符串请求行转换为Map
//        实现思路：把原有的字符串转换为json数据类型保存，通过ObjectMapper来转换为Map
//        Jackson json字符串--》map
//        1.实例化ObjectMapper对象
        ObjectMapper objectMapper = new ObjectMapper();
//        readValue方法参数解释
//        第一个参数：json字符串  第二个参数：转成的类型（map）
        Map headerMap = objectMapper.readValue(caseInfo.getRequestHeader(), Map.class);
        System.out.println(headerMap);

        Response res = given().
                headers(headerMap).
                body(caseInfo.getInputParams()).

                when().
                post("http://api.lemonban.com/futureloan" + caseInfo.getUrl()).
                then().log().body().

                extract().response();
        String expected = caseInfo.getExpected();
        ObjectMapper mapper2 = new ObjectMapper();
        Map expectedMap = mapper2.readValue(expected, Map.class);
        Set<Map.Entry<String,Object>> set = expectedMap.entrySet();
        for (Map.Entry<String, Object> map : set) {
//           关键点：做断言。通过Gpath获取实际接口响应对应字段的值
//            我们在Excel里面写用例的期望结果时，期望结果里面键名--->Gpath表达式
//            期望结果里面键值--->期望值
            Assert.assertEquals(res.path(map.getKey()),map.getValue());
        }

        //在登录模块用例执行结束之后将memberId保存到环境变量中
//        1。拿到正常用例返回响应信息里面的memberId
        Integer memberId =  res.path("data.id");
        if(memberId != null){
            //        2.保存到环境变量中
//            GlobalEnvironment.memberId = memberId;
//            System.out.println("11111111111111");
//            System.out.println(GlobalEnvironment.memberId);
            GlobalEnvironment.env.put("memberId",memberId);
            String token = res.path("data.token_info.token");
            GlobalEnvironment.env.put("token",token);
            System.out.println(token);
        }

    }

    @DataProvider
    public Object[] getLoginDatas(){

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
}
