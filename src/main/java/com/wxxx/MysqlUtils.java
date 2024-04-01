package com.wxxx;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;

/**
 *@description:
 *@author: ZhangLiqun
 *@date: 2024/4/1 15:12
 */
public class MysqlUtils {


	public static void main(String[] args) {
		validConnection();
	}
	public static Boolean validConnection() {

		Map<String, String> paramMap = PropertieUtils.resolveProperties();
		String host = paramMap.get("mysql.host");
		String port = paramMap.get("mysql.port");
		String username = paramMap.get("mysql.username");
		String password = paramMap.get("mysql.password");
		String database = paramMap.get("mysql.database");

		String url = "jdbc:mysql://" + host + ":" + port + "/" + database;

		Connection connection = null;
		try {
			// 尝试连接数据库
			connection = DriverManager.getConnection(url, username, password);
			System.out.println("Mysql连接成功");
		} catch (SQLException e) {
			System.out.println("Mysql连接失败,请检查用户名和密码");
			e.printStackTrace();
		} finally {
			// 关闭连接
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return true;
	}

}
