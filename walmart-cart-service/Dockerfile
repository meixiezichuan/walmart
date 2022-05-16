FROM registry.cn-hangzhou.aliyuncs.com/lavenpok/skywalking-agent:1.0
ARG NAME="walmart-cart-service"
ARG VERSION="1.0.0"

RUN cp /usr/share/zoneinfo/Asia/Shanghai /etc/localtime && echo 'Asia/Shanghai' >/etc/timezone
ENV SW_AGENT_NAME=$NAME
COPY ./target/$NAME-$VERSION.jar /app/walmart-cart-service.jar
CMD ["java", "-Xmx200m","-javaagent:/skywalking/agent/skywalking-agent.jar",  "-jar", "/app/walmart-cart-service.jar"]

EXPOSE 8684
