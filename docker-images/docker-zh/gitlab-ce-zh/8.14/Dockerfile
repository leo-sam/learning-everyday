FROM gitlab/gitlab-ce:8.14.2-ce.0
MAINTAINER denghui0115@163.com
RUN set -xe \
    && cp /usr/share/zoneinfo/Asia/Shanghai /etc/localtime \
    && VERSION=v8.14.2 \
    && echo " # Generating translation patch ..." \
    && git clone https://gitlab.com/xhang/gitlab.git \
    && cd gitlab \
    && git diff ${VERSION}..${VERSION}-zh > ../zh_CN.diff \
    && echo " # Patching ..." \
    && patch -d /opt/gitlab/embedded/service/gitlab-rails -p1 < ../zh_CN.diff \
    && echo " # Cleaning ..." \
    && cd .. \
    && rm -rf gitlab \
    && rm *.diff