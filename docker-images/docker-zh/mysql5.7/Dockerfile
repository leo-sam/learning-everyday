FROM mysql:5.7

# 维护者信息
MAINTAINER Deng Hui <denghui0115@sina.com>

RUN mv /etc/apt/sources.list /etc/apt/sources.list.bak && echo \
"deb http://mirrors.163.com/debian/ buster main contrib non-free \n"\
"deb http://mirrors.163.com/debian/ buster-updates main contrib non-free \n"\
"deb http://mirrors.163.com/debian/ buster-backports main contrib non-free \n"\
"deb http://mirrors.163.com/debian-security buster/updates main contrib non-free \n" > /etc/apt/sources.list 

RUN apt-get update && \
    apt-get install -y curl git unzip vim wget && \
    apt-get install -y locales kde-l10n-zhcn && \ 
    apt-get clean && \
    rm -rf /var/lib/apt/lists/* && \
    sed -i 's/# zh_CN.UTF-8 UTF-8/zh_CN.UTF-8 UTF-8/g' /etc/locale.gen && \
    locale && locale-gen "zh_CN.UTF-8"
       
# Set environment variables.
ENV LANG=zh_CN.UTF-8 \
    LANGUAGE=zh_CN:zh:en_US:en \
    LC_ALL=zh_CN.UTF-8 \
    TZ=Asia/Shanghai \
    DEBIAN_FRONTEND="noninteractive" \
    TERM=xterm

RUN ln -fs /usr/share/zoneinfo/$TZ /etc/localtime && \
echo $TZ > /etc/timezone && \        
dpkg-reconfigure --frontend noninteractive tzdata && \
dpkg-reconfigure --frontend noninteractive locales && \
echo \
"[mysqld]\n"\
"init_connect='SET collation_connection = utf8mb4_unicode_ci'\n"\
"init_connect='SET NAMES utf8mb4'\n"\
"character-set-server=utf8mb4\n"\
"collation-server=utf8mb4_unicode_ci\n"\
"skip-character-set-client-handshake\n"\
"[client]\n"\
"default-character-set=utf8mb4\n"\
"[mysql]\n"\
"default-character-set=utf8mb4\n" >> /etc/mysql/conf.d/docker.cnf

EXPOSE 3306
CMD ["mysqld"]

