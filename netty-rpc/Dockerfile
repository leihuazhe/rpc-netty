FROM openjdk:8-jdk-alpine
MAINTAINER Jeffrey # 295482300@qq.com
ARG JAR_FILE=*.jar
WORKDIR /
COPY --chmod=744 start.sh  /
COPY ${JAR_FILE} app.jar
CMD ["sh","-c","/start.sh"]