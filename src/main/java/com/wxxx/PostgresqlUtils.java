package com.wxxx;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;

/**
 *@description:
 *@author: ZhangLiqun
 *@date: 2024/4/1 15:35
 */
public class PostgresqlUtils {


	public static void main(String[] args) {
		validConnection();
	}
	public static Boolean validConnection() {

		Map<String, String> paramMap = PropertieUtils.resolveProperties();
		String host = paramMap.get("postgresql.host");
		String port = paramMap.get("postgresql.port");
		String username = paramMap.get("postgresql.username");
		String password = paramMap.get("postgresql.password");
		String database = paramMap.get("postgresql.database");

		String url = "jdbc:postgresql://" + host + ":" + port + "/" + database;

		Connection connection = null;
		try {
			// 尝试连接数据库
			connection = DriverManager.getConnection(url, username, password);
			System.out.println("PgSql连接成功");
		} catch (SQLException e) {
			System.out.println("PgSql连接失败,请检查用户名和密码");
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
