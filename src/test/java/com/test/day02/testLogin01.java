package com.test.day02;


import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.File;
import java.util.List;

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

public class testLogin01 {
    @Test(dataProvider = "getLoginDatas")
    public void testLogin(String url,String method,String header1,String header2,String jsonStr){
//        String jsonStr = "{\"mobile_phone\": \"15515341409\", \"pwd\": \"lemon66622\"}";
        given().
                header("Content-Type",header1).
                header("X-Lemonban-Media-Type",header2).
                body(jsonStr).

                when().
                        post("http://api.lemonban.com/futureloan/member/login").
                then().

                log().body();
    }
    @DataProvider
    public Object[][] getLoginDatas(){
        Object[][] datas = {{"http://api.lemonban.com/futureloan/member/login","post","application/json;charset=utf-8","lemonban.v1","{\"mobile_phone\": \"15515341409\", \"pwd\": \"lemon66622\"}"},
                {"http://api.lemonban.com/futureloan/member/login","post","application/json;charset=utf-8","lemonban.v1","{\"mobile_phone\": \"15515341409\", \"pwd\": \"lemon66622\"}"},
                {"http://api.lemonban.com/futureloan/member/login","post","application/json;charset=utf-8","lemonban.v1","{\"mobile_phone\": \"15515341409\", \"pwd\": \"lemon66622\"}"},
                {"http://api.lemonban.com/futureloan/member/login","post","application/json;charset=utf-8","lemonban.v1","{\"mobile_phone\": \"15515341409\", \"pwd\": \"lemon66622\"}"},
                {"http://api.lemonban.com/futureloan/member/login","post","application/json;charset=utf-8","lemonban.v1","{\"mobile_phone\": \"15515341409\", \"pwd\": \"lemon66622\"}"},
                {"http://api.lemonban.com/futureloan/member/login","post","application/json;charset=utf-8","lemonban.v1","{\"mobile_phone\": \"15515341409\", \"pwd\": \"lemon66622\"}"}


        };
        return datas;
    }
    public static void main(String[] args) {
        ImportParams importParams = new ImportParams();
//        sheet索引，默认起始值为0
        importParams.setStartSheetIndex(1);
////        要杜读取的sheet数量
//        importParams.setSheetNum(2);
        File excelFile = new File("src/test/resources/testcasehome1.xls");
        List<CaseInfo> list = ExcelImportUtil.importExcel(excelFile, CaseInfo.class, importParams);
        for (CaseInfo caseInfo : list) {
            System.out.println(caseInfo);
        }
    }
}
