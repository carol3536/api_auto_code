package com.test.day02;


import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.File;
import java.util.List;
import java.util.Map;

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
    @Test(dataProvider = "getLoginDatas02")
    public void testLogin(CaseInfo caseInfo) throws JsonProcessingException {
//        
        ObjectMapper objectMapper = new ObjectMapper();
        Map map = objectMapper.readValue(caseInfo.getRequestHeader(), Map.class);
        System.out.println(map);

//        given().
//                header("Content-Type",header1).
//                header("X-Lemonban-Media-Type",header2).
//                body(caseInfo.getInputParams()).
//
//                when().
//                        post(caseInfo.getUrl()).
//                then().
//
//                log().body();
    }
    @DataProvider
    public Object[] getLoginDatas02(){
//        dataprovider数据提供者返回值类型可以是Object[]也可以是Object[][]

        ImportParams importParams = new ImportParams();
//        sheet索引，默认起始值为0
        importParams.setStartSheetIndex(1);
////        要杜读取的sheet数量
//        importParams.setSheetNum(2);
        File excelFile = new File("src/test/resources/testcasehome1.xls");
        List<CaseInfo> list = ExcelImportUtil.importExcel(excelFile, CaseInfo.class, importParams);
        Object[] datas = list.toArray();
//        for (CaseInfo caseInfo : list) {
//            System.out.println(caseInfo);
//        }
        return datas;
    }

}
