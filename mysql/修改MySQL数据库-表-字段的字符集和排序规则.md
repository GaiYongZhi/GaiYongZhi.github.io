> 创建数据库并设置字符集编码和排序规则

```mysql
CREATE DATABASE `数据库名字` CHARACTER SET 'utf8mb4' COLLATE 'utf8mb4_general_ci';

```
> 修改数据库并设置字符集编码和排序规则

```mysql
ALTER DATABASE `数据库` CHARACTER SET 'utf8mb4' COLLATE 'utf8mb4_general_ci';
```
> 查询某个数据库下表的 排序规则 不为utf8mb4_general_ci的表

```mysql
SELECT
    TABLE_SCHEMA '数据库',
    TABLE_NAME '表',
    TABLE_COLLATION '原排序规则',
    CONCAT( 'ALTER TABLE `',TABLE_SCHEMA,'`.`', TABLE_NAME, '` CHARACTER SET=utf8mb4,  COLLATE=utf8mb4_general_ci;' ) '修正SQL' 
FROM
    information_schema.`TABLES` 
WHERE
    -- 修改为你的数据库名字
    TABLE_SCHEMA = '数据库' 
    AND TABLE_COLLATION != 'utf8mb4_general_ci'
    
```

> 查询某个数据库下表的字段 排序规则 不为utf8mb4_general_ci的数据

```mysql
SELECT
    TABLE_SCHEMA '数据库',
    TABLE_NAME '表',
    COLUMN_NAME '字段',
    CHARACTER_SET_NAME '原字符集',
    COLLATION_NAME '原排序规则',
    CONCAT(
    'ALTER TABLE `',
    TABLE_SCHEMA,'`.`',
    TABLE_NAME,
    '` MODIFY COLUMN ',
    COLUMN_NAME,
    ' ',
    COLUMN_TYPE,
    -- - 设置新的编码和排序规则
    ' CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci',
    ( CASE WHEN IS_NULLABLE = 'NO' THEN ' NOT NULL' ELSE '' END ),
    ( CASE WHEN COLUMN_COMMENT = '' THEN ' ' ELSE concat( ' COMMENT''', COLUMN_COMMENT, '''' ) END ),
    ';' 
) '修正SQL' 
FROM
    information_schema.`COLUMNS` 
WHERE
    -- -过滤正确排序规则
    COLLATION_NAME != 'utf8mb4_general_ci'
     -- 修改为你的数据库名字
    AND TABLE_SCHEMA = '数据库';
```
