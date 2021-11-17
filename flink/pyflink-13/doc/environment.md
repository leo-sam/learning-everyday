# 基础环境

```python
brew install pyenv
# 安装指定版本
pyenv install 3.8.12
# 安装新版本后rehash一下
pyenv rehash

--- ~/.zshrc ----
export PYENV_ROOR="$HOME/.pyenv"
export PATH=$PYENV_ROOT/shims:$PATH
eval "$(pyenv init -)"
-----------------

# update
python -m pip install --upgrade pip
# install pyflink
python -m pip install apache-flink==1.13.3
```

# 依赖

## Kafka

```python
# 执行kafka之前把依赖的jar包下载在flink目录
# wget -P ${FLINK_HOME}/lib/ http://mirrors.cloud.tencent.com/nexus/repository/maven-public/org/apache/flink/flink-sql-connector-kafka_2.11/1.12.5/flink-sql-connector-kafka_2.11-1.12.5.jar
```

## Hadoop

```python
# 支持hdfs需要添加jar包到flink目录flink-shaded-hadoop-2-uber-2.8.3-9.0.jar（版本用最新的就行）
# wget -P ${FLINK_HOME}/lib/ https://mirrors.cloud.tencent.com/nexus/repository/maven-public/org/apache/flink/flink-shaded-hadoop-2-uber/2.8.3-9.0/flink-shaded-hadoop-2-uber-2.8.3-9.0.jar
```