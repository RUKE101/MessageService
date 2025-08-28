# Stage 1: Build
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# Копируем pom и скачиваем зависимости
COPY pom.xml .
RUN mvn dependency:go-offline

# Копируем исходники
COPY src ./src

# Сборка приложения
RUN mvn clean package -DskipTests
FROM eclipse-temurin:21-jre
# Устанавливаем /app как рабочую директорию
WORKDIR /app
# Копируем джарники
COPY --from=build /app/target/*.jar app.jar
# Запускаем
ENTRYPOINT ["java","-jar","app.jar"]