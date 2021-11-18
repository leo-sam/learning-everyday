# debugging

## 如何查看Sink的流的值对不对

How to check whether the value of Sink's stream is correct

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

# Coding

## 如何**组织多条语句一起执行**

How do I organize multiple statements to execute together

```python
# create a statement set
statement_set = t_env.create_statement_set()

# emit the data with id <= 3 to the "first_sink" via sql statement
statement_set.add_insert_sql("INSERT INTO first_sink SELECT * FROM %s WHERE id <= 3" % table)

# emit the data which contains "Flink" to the "second_sink"
@udf(result_type=DataTypes.BOOLEAN())
def contains_flink(data):
    return "Flink" in data

second_table = table.where(contains_flink(table.data))
statement_set.add_insert("second_sink", second_table)

# execute the statement set
# remove .wait if submitting to a remote cluster, refer to
# https://nightlies.apache.org/flink/flink-docs-stable/docs/dev/python/faq/#wait-for-jobs-to-finish-when-executing-jobs-in-mini-cluster
# for more details
statement_set.execute().wait()
```

## 如何打log

How to output log

```python
import logging
import sys

if __name__ == '__main__':
    logging.basicConfig(stream=sys.stdout, level=logging.INFO, format="%(message)s")
```

## 如何把table变量结合到sql中

How to combine a table variable into SQL

```python
t_env = TableEnvironment.create(EnvironmentSettings.new_instance().in_streaming_mode().build())

table = t_env.from_elements(
    elements=[(1, 'Hello'), (2, 'World'), (3, "Flink"), (4, "PyFlink")],
    schema=['id', 'data'])

# emit the data with id <= 3 to the "first_sink" via sql statement
statement_set.add_insert_sql("INSERT INTO first_sink SELECT * FROM %s WHERE id <= 3" % table)
```

## 如何给代码添加checkpoint到hdfs中

How do I checkpoint code to HDFS

- 支持hadoop需要下载shaded包

```
# 支持hdfs需要添加jar包到flink目录flink-shaded-hadoop-2-uber-2.8.3-9.0.jar（版本用最新的就行）
# wget -P ${FLINK_HOME}/lib/ https://mirrors.cloud.tencent.com/nexus/repository/maven-public/org/apache/flink/flink-shaded-hadoop-2-uber/2.8.3-9.0/flink-shaded-hadoop-2-uber-2.8.3-9.0.jar

```

- 代码

```python
from pyflink.datastream import StreamExecutionEnvironment, FsStateBackend
from pyflink.table import EnvironmentSettings, StreamTableEnvironment

env = StreamExecutionEnvironment.get_execution_environment()
# 设置state_backend
env.set_state_backend(FsStateBackend("hdfs://10.0.xx.xx:xxx/flink/checkpoints"))
# 每 1000ms 开始一次 checkpoint
env.enable_checkpointing(1000)
# Checkpoint 必须在一分钟内完成，否则就会被抛弃
env.get_checkpoint_config().set_checkpoint_timeout(60000)
# 同一时间只允许一个 checkpoint 进行
env.get_checkpoint_config().set_max_concurrent_checkpoints(1)
# 允许在有更近 savepoint 时回退到 checkpoint
env.get_checkpoint_config().set_prefer_checkpoint_for_recovery(True)
# setting
env_settings = EnvironmentSettings.new_instance().in_streaming_mode().use_blink_planner().build()
# init table env
table_env = StreamTableEnvironment.create(env, environment_settings=env_settings)
```