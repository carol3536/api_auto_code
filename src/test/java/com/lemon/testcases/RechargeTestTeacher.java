package com.lemon.testcases;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lemon.base.BaseCase;
import com.lemon.pojo.CaseInfo;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static io.restassured.RestAssured.given;
import static io.restassured.config.JsonConfig.jsonConfig;
import static io.restassured.path.json.config.JsonPathConfig.NumberReturnType.BIG_DECIMAL;

public class RechargeTestTeacher extends BaseCase {

    List<CaseInfo> caseInfoList;

    @BeforeClass
    public void setup(){
        //从Excel读取用户信息接口模块所需要的用例数据
        caseInfoList = getCaseDataFromExcel(4);
        //参数化替换
        caseInfoList = paramReplace(caseInfoList);
    }

    @Test(dataProvider = "getRechageDatas")
    public void testRecharge(CaseInfo caseInfo) throws JsonProcessingException {
        //请求头由json字符串转Map
        ObjectMapper objectMapper = new ObjectMapper();
        Map headersMap = objectMapper.readValue(caseInfo.getRequestHeader(), Map.class);
        //发起接口请求
        Response res =
                given().
                        //让REST-Assured返回json小数的时候，使用BigDecimal类型来存储小数（默认是Float存储的）
                        config(RestAssured.config().jsonConfig(jsonConfig().numberReturnType(BIG_DECIMAL))).
                        headers(headersMap).
                        body(caseInfo.getInputParams()).
                when().
                        post("http://api.lemonban.com/futureloan"+caseInfo.getUrl()).
                then().log().all().
                        extract().response();
        //断言
        assertExpected(caseInfo,res);
    }

    @DataProvider
    public Object[] getRechageDatas(){
        //dataprovider数据提供者返回值类型可以是Object[] 也可以是Object[][]
        //怎么list集合转换为Object[][]或者Object[]？？？
        return caseInfoList.toArray();
        //datas一维数组里面保存其实就是所有的CaseInfo对象
    }

    public static void main(String[] args) {
        //Double ？ double？？
        Double a = 0.01;
        Float b = 0.01f;
        if(a instanceof Double){
            System.out.println("是的");
        }
        //类型不一致会导致断言失败
        //BigDecimal -->大的小数 用它来进行运算可以避免精度的丢失（金额）
        //把原始的类型Float/Double转化成为BigDecimal
        //rest-assured如果接口响应结果返回是json，并且json里面有小数，你用Gpath表达式获取结果的时候用Float来存储
        //1、解决方案：Gpath表达式获取结果的时候用BigDecimal来存储（实际值）
        //2、把期望值也转化成BigDecimal
        //BigDecimal bigDecimala = new BigDecimal(a.toString());
        //BigDecimal bigDecimalb = new BigDecimal(b.toString());
        //Assert.assertEquals(bigDecimala,bigDecimalb);
    }

}
