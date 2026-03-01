# Dockerfile
FROM openjdk:21-jdk-slim as builder

WORKDIR /app

# Копируем файлы сборки
COPY gradle ./gradle
COPY gradlew .
COPY build.gradle.kts .
COPY settings.gradle.kts .
COPY src ./src

# Даем права на выполнение gradlew
RUN chmod +x gradlew

# Собираем приложение
RUN ./gradlew clean build -x test

# Production образ
FROM openjdk:21-jdk-slim

WORKDIR /app

# Устанавливаем зависимости для здоровья приложения
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

# Создаем пользователя для безопасности
RUN groupadd -r spring && useradd -r -g spring spring
USER spring

# Копируем собранный JAR из builder стадии
COPY --from=builder /app/build/libs/*.jar app.jar

# Настройки JVM для контейнера
ENV JAVA_OPTS="-Xmx512m -Xms256m -Djava.security.egd=file:/dev/./urandom"

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]