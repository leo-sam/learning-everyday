### 创建git目录

rm -r -f /tmp/docker-zh                                                               \
git clone https://gitee.com/denghui0115/docker-zh.git /tmp/docker-zh                  

### 查找镜像tags的脚本命令
- 拷贝脚本到当前目录,修改运行权限,执行命令 \
rm -r -f ./docker-tags.sh    \
cp /tmp/docker-zh/docker-tags.sh ./docker-tags.sh \
chmod 755 ./docker-tags.sh \
sed -i 's/\r$//' ./docker-tags.sh \
./docker-tags.sh alpine 

### 下载用于制作修改时区的基础镜像文件

docker pull alpine:3.4         \
docker pull centos:7           \
docker pull ubuntu:16.04       \
docker pull debian:jessie 

### 制作修改时区、字符集的基础镜像文件

docker build --rm -t centos:7-zh /tmp/docker-zh/centos/7/                                  \
docker build --rm -t ubuntu:16.04-zh /tmp/docker-zh/ubuntu/16.04/                          \
docker build --rm -t alpine:3.4-zh /tmp/docker-zh/alpine/3.4/                              \
docker build --rm -t alpine:3.9-zh /tmp/docker-zh/alpine/3.9/                              \
docker build --rm -t debian:jessie-zh /tmp/docker-zh/debian/jessie/                        \
docker build --rm -t debian:buster-slim-tz.8 /tmp/docker-zh/debian/buster/                 \
docker build --rm -t mysql:5.7-tz.8 /tmp/docker-zh/mysql5.7/                               \
docker build --rm -t baidupcs-web:3.7-zh /tmp/docker-zh/baidupcs-web/       

### 测试基础镜像时区、字符集是否设置成功：

docker run -it --rm centos:7-zh /bin/bash             \
docker run -it --rm ubuntu:16.04-zh /bin/bash         \
docker run -it --rm alpine:3.9-zh /bin/bash           \
docker run -it --rm debian:jessie-zh /bin/bash

### 镜像系统时间、字符集的查询方法 

date 命令查看系统时间是否与机器时间一致
locale 命令查看系统字符集

