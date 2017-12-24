package com.htroy.cmccportcheck;

import java.sql.Connection;
import java.sql.DriverManager;

public class MysqlConnection {
    public static Connection getConnection(String user, String password) throws Exception {
        Connection con;
        String DB_URL = "jdbc:mysql://#dbhost/#db"; // please set the host
        Class.forName("com.mysql.jdbc.Driver");
        con = DriverManager.getConnection(DB_URL, user, password);
        System.out.println("连接数据库成功");
        return con;
    }

    public static void closeConnection(Connection con) throws Exception {
        con.close();
    }
}
