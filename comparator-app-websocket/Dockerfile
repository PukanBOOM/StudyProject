FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY comparator-app-websocket/build/libs/*-all.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]