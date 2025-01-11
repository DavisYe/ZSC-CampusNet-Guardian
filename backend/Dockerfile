# Dependencies stage
FROM maven:3.8-openjdk-17 AS deps
WORKDIR /app

# Configure Maven to use Huawei mirror
RUN mkdir -p /root/.m2 \
    && echo '<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0" \
      xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" \
      xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0 \
      https://maven.apache.org/xsd/settings-1.0.0.xsd"> \
      <mirrors> \
        <mirror> \
          <id>huaweicloud</id> \
          <mirrorOf>*</mirrorOf> \
          <url>https://repo.huaweicloud.com/repository/maven/</url> \
        </mirror> \
      </mirrors> \
    </settings>' > /root/.m2/settings.xml

# Copy only pom.xml
COPY pom.xml .

# Download dependencies
RUN mvn dependency:go-offline

# Builder stage
FROM maven:3.8-openjdk-17 AS builder
WORKDIR /app

# Copy Maven settings from previous stage
COPY --from=deps /root/.m2/settings.xml /root/.m2/settings.xml

# Copy deps from previous stage
COPY --from=deps /root/.m2/repository /root/.m2/repository
COPY . .

# Build with debug output
RUN mvn clean package -DskipTests \
    && echo "=== Listing target directory ===" \
    && ls -la target \
    && echo "=== Checking for JAR file ===" \
    && find /app -name "*.jar" -type f \
    && echo "=== JAR file details ===" \
    && ls -la target/backend-1.0.0.jar

# Production stage
FROM openjdk:17-jdk-slim
WORKDIR /app

# Debug: List contents before copy
RUN echo "=== Contents of /app before copy ===" && ls -la /app

# Copy JAR file with explicit name and debug output
COPY --from=builder /app/target/backend-1.0.0.jar /app/app.jar

# Debug: List contents and verify permissions after copy
RUN echo "=== Contents of /app after copy ===" \
    && ls -la /app \
    && echo "=== Setting permissions ===" \
    && chmod 755 /app/app.jar \
    && ls -la /app/app.jar \
    && echo "=== File permissions ===" \
    && stat /app/app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]