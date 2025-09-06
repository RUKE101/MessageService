FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Копируем pom и скачиваем зависимости
COPY pom.xml .
RUN mvn dependency:go-offline

# Копируем исходники
COPY src ./src

# Сборка приложения
RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jre
WORKDIR /app

# Копируем джарники
COPY --from=build /app/target/*.jar app.jar

# Запуск с Parallel GC
ENTRYPOINT ["java", "-XX:+UseParallelGC", "-jar", "app.jar"]
