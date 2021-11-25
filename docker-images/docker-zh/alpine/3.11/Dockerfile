FROM alpine:3.11

MAINTAINER Deng Hui <denghui0115@163.com>

# 使用国内alpine源
RUN sed -i 's/dl-cdn.alpinelinux.org/mirrors.aliyun.com/g' /etc/apk/repositories

# Install base packages
RUN apk --no-cache update && \
    apk --no-cache upgrade && \
    apk --no-cache add curl bash tzdata tar unzip && \ 
    ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime && \
    echo "Asia/Shanghai" > /etc/timezone && \
    echo -ne "Alpine Linux 3.11 image. (`uname -rsv`)\n" >> /root/.built && \
    rm -fr /tmp/* /var/cache/apk/*

# Define bash as default command
CMD ["/bin/bash"]

# docker build --rm -t alpine:3.11-tz.8 .
# docker rm -f test
# docker run -d -ti --name test alpine:3.11-tz.8
# docker exec -ti test /bin/bash