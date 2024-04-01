package com.wxxx;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;

/**
 *@description:
 *@author: ZhangLiqun
 *@date: 2024/4/1 14:57
 */
public class PropertieUtils {

	private static Map<String, String> paramMap = new HashMap();

	public static void main(String[] args) {
		System.out.println(resolveProperties());
	}

	public static Map<String, String> resolveProperties() {
		String rootLocation = System.getProperty("user.dir");
		String paramLocation = rootLocation + "/src/main/resources/application.properties";
		try (BufferedReader reader = new BufferedReader(new FileReader(paramLocation))) {
			String line;
			while ((line = reader.readLine()) != null) {
				if (StringUtils.isNotEmpty(line)) {
					String[] split = line.split("=");
					String key = split[0];
					key = key.trim();
					String value = split[1];
					value = value.trim();
					if (StringUtils.isEmpty(value)) {
						throw new RuntimeException("配置项:" + key + "不能为空");
					}
					paramMap.put(key, value);
				}
			}
		} catch (Exception e) {
			throw new RuntimeException("配置文件解析异常，请检查配置文件格式，严格按照 xxx=xxx格式书写");
		}
		return paramMap;
	}

}
