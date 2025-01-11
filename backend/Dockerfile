# Dependencies stage
FROM maven:3.8-openjdk-17 AS deps
WORKDIR /app

# Copy only pom.xml
COPY pom.xml .

# Download dependencies
RUN mvn dependency:go-offline

# Builder stage
FROM maven:3.8-openjdk-17 AS builder
WORKDIR /app

# Copy deps from previous stage
COPY --from=deps /root/.m2 /root/.m2
COPY . .

# Build
RUN mvn clean package -DskipTests

# Production stage
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"] 