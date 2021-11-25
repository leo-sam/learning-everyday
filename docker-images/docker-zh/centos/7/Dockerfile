FROM centos:7

MAINTAINER Deng Hui <denghui0115@sina.com>

RUN curl -o /etc/yum.repos.d/CentOS-Base.repo http://mirrors.163.com/.help/CentOS7-Base-163.repo

RUN yum -y update && \
    yum -y install git unzip vim wget && \
    yum -y install kde-l10n-Chinese bitmap-fonts bitmap-fonts-cjk && \
    #yum -y reinstall glibc-common && \
    yum clean all

RUN locale 

# Set environment variables.
ENV HOME /root
ENV LANG=en_US.UTF-8\
    LANGUAGE=zh_CN:zh:en_US:en\
    LC_ALL=en_US.UTF-8\
    TZ=Asia/Shanghai\
    TERM=xterm

RUN unlink /etc/localtime && \
    ln -sf /usr/share/zoneinfo/$TZ /etc/localtime && \
    echo $TZ > /etc/timezone 


# docker build --rm -t centos:7-tz.8 .
# docker rm -f test
# docker run -d -ti --name test centos:7-tz.8
# docker exec -ti test /bin/bash