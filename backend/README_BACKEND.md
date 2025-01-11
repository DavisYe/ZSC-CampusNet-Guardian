/# 后端项目说明

## 项目概述
本项目是一个基于Spring Boot的后端服务。

## 技术栈
- Java 17
- Spring Boot 3.x
- Maven

## 开发环境配置
1. 确保已安装JDK 17
2. 构建项目：
   ```bash
   ./mvnw clean install
   ```
3. 启动应用：
   ```bash
   ./mvnw spring-boot:run
   ```

## 项目结构
- `src/main/java/`：Java源代码
- `src/main/resources/`：配置文件
- `pom.xml`：Maven配置文件
- `compose.yaml`：Docker Compose配置