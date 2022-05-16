FROM registry.cn-hangzhou.aliyuncs.com/lavenpok/skywalking-agent:1.0
ARG NAME="walmart-user-service"
ARG VERSION="1.0.0"
ENV SW_AGENT_NAME=$NAME
RUN cp /usr/share/zoneinfo/Asia/Shanghai /etc/localtime && echo 'Asia/Shanghai' >/etc/timezone

COPY ./target/$NAME-$VERSION.jar /app/walmart-user-service.jar
CMD ["java", "-Xmx200m", "-javaagent:/skywalking/agent/skywalking-agent.jar","-jar", "/app/walmart-user-service.jar"]

EXPOSE 8680
