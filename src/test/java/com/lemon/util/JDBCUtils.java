package com.lemon.util;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.MapHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.testng.IResultMap;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * @Project: api_auto_test
 * @Site: http://www.lemonban.com
 * @Forum: http://testingpai.com
 * @Copyright: 2020 版权所有 湖南省零檬信息技术有限公司
 * @Author: Carol
 * @Create: 2020-12-25 15:29
 * @Desc：
 **/

public class JDBCUtils {
    public static Connection getConnection(){
//        定义数据库连接
//        Oracle:jdbc:oracle:thin:@localhost:1521:DBName
//        SqlServer:jdbc:microsoft:sqlserver://localhost:1433;DatabaseName=DBName
//        MySql:jdbc:mysql://localhost:3306/DBName
        String url = "jdbc:mysql://8.129.91.152:3306/futureloan?userUnicode=true&characterEncoding=utf-8";
        String user = "future";
        String password = "123456";
//        定义数据库连接对象
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url,user,password);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return conn;
    }

    public static void main(String[] args) {
//1. 新增数据
//        String sqlStr = "insert into member values(146556666,'carol','12345678','18851983607',1,1000.0,'2020-11-11 16:38:11');";
//        update(sqlStr);
//        2.更改数据
        String sqlStr1 = "update member set reg_name = 'liuxx' where mobile_phone='18851983607';";
        update(sqlStr1);
//        3.删除数据  一般没有权限，了解
//        String sqlStr2 = "delete from member where moblie_phone = '18851983536'";
//        update(sqlStr2);
//        4.查询数据  数据的ID 0-9的数据
        String sqlStr3 = "select * from member limit 10;";
//        String sqlStr3 = "select * from member where type = 0;";
        List<Map<String, Object>> maps = queryAll(sqlStr3);
        for (Map<String, Object> map : maps) {
            System.out.println(map);
        }
//        5.查询返回一条数据
        String sqlStr4 = "select * from member where mobile_phone = 18851983607;";
        Map<String, Object> map = queryOne(sqlStr4);
        System.out.println(map);
        //        6.查询返回一个结果，一个数据，一个字符串。。。。
        String sqlStr5 = "select count(*) from member where mobile_phone = 18851983607";
        Object o = querySingle(sqlStr5);
        System.out.println(o);

    }
    public static void update(String sqlStr){
        //        1.获取数据库连接对象
        Connection conn = getConnection();
//        2.数据库新增操作
        QueryRunner queryRunner = new QueryRunner();
        try {
            queryRunner.update(conn,sqlStr);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
//        3.关闭数据库连接
        try {
            conn.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
//    结果是多条数据
    public static List<Map<String, Object>> queryAll(String sqlStr){
        //        1.获取数据库连接对象
        Connection conn = getConnection();
//        2.数据库新增操作
        QueryRunner queryRunner = new QueryRunner();
        try {
//            第一个参数：连接对象，第二个参数：SQL语句，第三个参数：接收查询结果
            List<Map<String, Object>> result = queryRunner.query(conn, sqlStr, new MapListHandler());
            return result;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
//        3.关闭数据库连接
        try {
            conn.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }
//    结果是一条数据
    public static Map<String, Object> queryOne(String sqlStr){
        //        1.获取数据库连接对象
        Connection conn = getConnection();
//        2.数据库新增操作
        QueryRunner queryRunner = new QueryRunner();
        try {
//            第一个参数：连接对象，第二个参数：SQL语句，第三个参数：接收查询结果
            Map<String, Object> result = queryRunner.query(conn, sqlStr, new MapHandler());
            return result;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
//        3.关闭数据库连接
        try {
            conn.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }
    //    结果是一个数据
    public static Object querySingle(String sqlStr){
        //        1.获取数据库连接对象
        Connection conn = getConnection();
//        2.数据库新增操作
        QueryRunner queryRunner = new QueryRunner();
        try {
//            第一个参数：连接对象，第二个参数：SQL语句，第三个参数：接收查询结果
            Object result = queryRunner.query(conn, sqlStr, new ScalarHandler<Object>());
            return result;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
//        3.关闭数据库连接
        try {
            conn.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }
}


