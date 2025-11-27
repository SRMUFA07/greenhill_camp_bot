# Используем официальный образ Maven с Java 21
FROM maven:3.9-eclipse-temurin-21 AS builder
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests -Dmaven.compiler.release=21

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
COPY --from=builder /app/src/main/resources/images ./images
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]