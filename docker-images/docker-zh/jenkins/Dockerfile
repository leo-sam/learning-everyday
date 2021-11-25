FROM jenkins/jenkins:lts-alpine

# if we want to install via apt
USER root
RUN rm -rf /etc/localtime && cp /usr/share/zoneinfo/Asia/Shanghai /etc/localtime \
    && mkdir /etc/sysconfig -p \
    && echo -e 'ZONE="Asia/Shanghai"\nUTC=false\nARC=false' > /etc/sysconfig/clock \
    && echo 'Asia/Shanghai' > /etc/timezone

# drop back to the regular jenkins user - good practice
USER jenkins