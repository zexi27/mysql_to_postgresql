#!/bin/bash

# PostgreSQL连接信息
pg_user="postgres"
pg_password="zaxscdvf"
pg_db="notb1"
pg_host="192.168.28.34"
pg_port="5432"

# 从命令行参数中获取表名和主键信息，并将其同步到PostgreSQL中



while IFS= read -r line; do
    # 解析参数中的表名和主键列表
    table=$(echo "$line" | awk -F '[:[]' '{print $1}')
    keys=$(echo "$line" | awk -F '[][]' '{print $2}')

    # 检查表是否已经有主键
    existing_keys=$(PGPASSWORD="$pg_password" psql -h "$pg_host" -p "$pg_port" -U "$pg_user" -d "$pg_db" -c "SELECT constraint_name FROM information_schema.table_constraints WHERE table_name = '$table' AND constraint_type = 'PRIMARY KEY';")
    # 如果表已经有主键，则跳过当前表
    if [[ $existing_keys == *"0 rows"* ]]; then
      # 执行SQL语句
      PGPASSWORD="$pg_password" psql -h "$pg_host" -p "$pg_port" -U "$pg_user" -d "$pg_db" -c "ALTER TABLE "$table" ADD CONSTRAINT pk_"$table" PRIMARY KEY ("$keys");"
      # 检查SQL执行结果
      if [ $? -eq 0 ]; then
          echo "已成功将表 $table 的主键设置为 $keys"
      else
          echo "设置表 $table 的主键时出错"
      fi
    else
      echo "表 $table 已经存在主键，跳过设置"
    fi
    echo "======================================="
    continue

done <<< "$1"
