# 基础镜像
FROM openjdk:8
# 作者
MAINTAINER cjc
# 配置
ENV PARAMS=""
# 时区
ENV TZ=PRC
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
# 添加应用
#ADD target/chatgpt-api-1.0.jar /chatgpt-api-1.0.jar
#注意该路径需要没有其他jar包
ADD D:/new-chatgpt-data/chatgpt-data-app/target/chatgpt-data-app.jar /chatgpt-data-app-1.0.jar
## 在镜像运行为容器后执行的命令
ENTRYPOINT ["sh","-c","java -jar $JAVA_OPTS /chatgpt-data-app-1.0.jar $PARAMS"]