# debugging

## 如何查看Sink的流的值对不对

通过把`connector`改为`print`

```python
# 1. 创建 TableEnvironment
env_settings = EnvironmentSettings.new_instance().in_streaming_mode().use_blink_planner().build()
table_env = TableEnvironment.create(env_settings) 
# 2. 创建 source 表
table_env.execute_sql("""
    CREATE TABLE datagen (
        id INT,
        data STRING
    ) WITH (
        'connector' = 'datagen',
        'fields.id.kind' = 'sequence',
        'fields.id.start' = '1',
        'fields.id.end' = '10'
    )
""")
# 3. 创建 sink 表
table_env.execute_sql("""
    CREATE TABLE print (
        id INT,
        data STRING
    ) WITH (
        'connector' = 'print'
    )
""")
# 4. 查询 source 表，同时执行计算
# 通过 Table API 创建一张表：
source_table = table_env.from_path("datagen")
# 或者通过 SQL 查询语句创建一张表：
source_table = table_env.sql_query("SELECT * FROM datagen")
result_table = source_table.select(source_table.id + 1, source_table.data)
# 5. 将计算结果写入给 sink 表
# 将 Table API 结果表数据写入 sink 表：
result_table.execute_insert("print").wait()
# 或者通过 SQL 查询语句来写入 sink 表：
table_env.execute_sql("INSERT INTO print SELECT * FROM datagen").wait()
```