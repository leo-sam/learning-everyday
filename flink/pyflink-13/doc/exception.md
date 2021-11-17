# TIMESTAMP

```python
Caused by: java.time.format.DateTimeParseException: Text '1637150408680' could not be parsed at index 0
```

```python
# 之前代码
CREATE TABLE eventIn (
          fromTs TIMESTAMP(3)
       )
# 修改之后代码
CREATE TABLE eventIn (
          fromTs BIGINT,
          fromT AS TO_TIMESTAMP(FROM_UNIXTIME(fromTs/1000, 'yyyy-MM-dd HH:mm:ss')),
         )
```

原因：pyflink中的kafka connectior `TIMESTAMP`需要满足`yyyy-MM-dd HH:mm:ss.SSS`格式