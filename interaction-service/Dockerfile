FROM openjdk:17-jdk-alpine
EXPOSE 8082
ADD build/**/*.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]