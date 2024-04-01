package com.wxxx;

/**
 *@description:
 *@author: ZhangLiqun
 *@date: 2024/4/1 11:24
 */
import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @description:
 * @author: ZhangLiqun
 * @date: 2024/3/27 11:04
 */
public class PrimaryKeyUtils {

    public static void main(String[] args) throws Exception {

        Scanner scanner = new Scanner(System.in);
        System.out.println("请输入.sql文件全路径");
        String filePath = scanner.next();
        File file = new File(filePath);
        if (!file.exists()){
            throw new Exception("sql文件不存在");
        }
        System.out.println("请输入shell执行脚本路径");
        String shellPath = scanner.next();
        file = new File(filePath);
        if (!file.exists()){
            throw new Exception("脚本文件不存在");
        }

        Map<String, List<String>> tblNameKeyMap = resolveSqlFile(filePath);
        String paramText = generateShellParam(tblNameKeyMap);
        invokeShell(shellPath,paramText);

    }

    private static void invokeShell(String shellPath, String paramText) {
        String console = "开始调用shell脚本...";
        System.out.println(console);
        try {
            // 定义Bash脚本命令
            List<String> commandList = new ArrayList<>();
            commandList.add("/bin/bash");
            commandList.add(shellPath);
            commandList.add(paramText);
            // 创建ProcessBuilder对象并传入命令
            ProcessBuilder pb = new ProcessBuilder(commandList);

            // 启动进程
            Process process = pb.start();

            // 获取进程输出
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
            // 等待进程执行完毕
            int exitCode = process.waitFor();

            // 打印进程退出码
            System.out.println("Process exited with code " + exitCode);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static String generateShellParam(Map<String, List<String>> tblNameKeyMap) {
        String console = "正在生成脚本执行参数...";
        System.out.print(console);
        StringBuilder paramTxtBuilder = new StringBuilder();
        StringBuilder primaryKeyBuilder;
        for (Map.Entry<String, List<String>> entry : tblNameKeyMap.entrySet()) {
            String tableName = entry.getKey();
            List<String> primaryKeys = entry.getValue();
            primaryKeyBuilder = new StringBuilder();
            primaryKeyBuilder.append("[");
            for (String primaryKey : primaryKeys) {
                primaryKeyBuilder.append(primaryKey);
            }
            primaryKeyBuilder.append("]");
            paramTxtBuilder.append(tableName)
                    .append(":")
                    .append(primaryKeyBuilder)
                    .append("\n");
        }
        if (paramTxtBuilder.length() > 0 && paramTxtBuilder.charAt(paramTxtBuilder.length() - 1) == '\n') {
            paramTxtBuilder.deleteCharAt(paramTxtBuilder.length() - 1);
        }
        System.out.println("完成");
        return paramTxtBuilder.toString();
    }

    private static Map<String, List<String>> resolveSqlFile(String filePath)  {
        String tableNameReg = "CREATE TABLE `(\\w+)`";
        String primaryKeyNameReg = "PRIMARY KEY \\(`(\\w+)`\\)";
        String console = "正在解析.sql文件，获取表名和主键名称...";
        System.out.print(console);
        Map<String, List<String>> tblNameKeyMap = new HashMap<>(); // key-表名 value-主键名称，适配含有多个主键
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            String curTableName = "";
            List<String> primaryKeyList = new ArrayList<>();
            while ((line = reader.readLine()) != null) {
                if (line != "" && line.startsWith("CREATE TABLE")) {
                    if (!curTableName.equals("") && !primaryKeyList.isEmpty()) {
                        tblNameKeyMap.put(curTableName, primaryKeyList); // 放置上一个表名和主键集合
                    }
                    primaryKeyList = new ArrayList<>();
                    Pattern pattern = Pattern.compile(tableNameReg);
                    Matcher matcher = pattern.matcher(line);
                    if (matcher.find()) {
                        curTableName = matcher.group(1);
                    }
                }
                if (line != "" && line.contains("PRIMARY KEY")) {
                    Pattern pattern = Pattern.compile(primaryKeyNameReg);
                    Matcher matcher = pattern.matcher(line);
                    if (matcher.find()) {
                        String primaryKeyName = matcher.group(1);
                        primaryKeyList.add(primaryKeyName);
                    }
                }
            }

            if (!curTableName.equals("") && !primaryKeyList.isEmpty()) {
                tblNameKeyMap.put(curTableName, primaryKeyList); // 放置上一个表名和主键集合
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        System.out.println("完成");
        return tblNameKeyMap;
    }
}
