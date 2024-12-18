FROM openjdk:17-jdk-alpine
EXPOSE 8081
ADD build/**/*.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]