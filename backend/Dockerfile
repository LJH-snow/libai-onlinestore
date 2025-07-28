# 使用OpenJDK 21作为基础镜像
FROM openjdk:21-jdk-slim

# 设置工作目录
WORKDIR /app

# 复制Maven配置文件
COPY pom.xml .
COPY mvnw .
COPY mvnw.cmd .
COPY .mvn .mvn

# 复制源代码
COPY src src

# 设置Maven环境变量
ENV MAVEN_HOME=/usr/share/maven
ENV PATH=$PATH:$MAVEN_HOME/bin

# 更换apt源为阿里云，加速安装（适配slim镜像）
RUN echo "deb http://mirrors.aliyun.com/debian/ bookworm main contrib non-free non-free-firmware\n\
deb http://mirrors.aliyun.com/debian/ bookworm-updates main contrib non-free non-free-firmware\n\
deb http://mirrors.aliyun.com/debian-security bookworm-security main contrib non-free non-free-firmware" > /etc/apt/sources.list

# 安装Maven
RUN apt-get update && apt-get install -y maven

# 构建应用
RUN mvn clean package -DskipTests

# 暴露端口
EXPOSE 8081

# 启动应用
CMD ["java", "-jar", "target/backend-0.0.1-SNAPSHOT.jar"] 