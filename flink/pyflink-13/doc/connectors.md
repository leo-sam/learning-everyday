# Kafka

```python
	kafka_servers = "10.xx.xx.xx:9092"
    source_topic = "xx-cvt"
    kafka_consumer_group_id = "group_id1"

    table_env.execute_sql(f"""
        CREATE TABLE eventIn (
          fromTs BIGINT
          fromT AS TO_TIMESTAMP(FROM_UNIXTIME(fromTs/1000, 'yyyy-MM-dd HH:mm:ss'))
        ) WITH (
              'connector' = 'kafka',
              'topic' = '{source_topic}',
              'properties.bootstrap.servers' = '{kafka_servers}',
              'properties.group.id' = '{kafka_consumer_group_id}',
              'scan.startup.mode' = 'earliest-offset',
              'format' = 'json'
        )
    """)
```