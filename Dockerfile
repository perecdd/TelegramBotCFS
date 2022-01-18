FROM openjdk:14-jdk-alpine
COPY target/swagger-spring-1.0.0.jar telegram-bot/swagger-spring-1.0.0.jar
ENTRYPOINT ["java","-jar","/telegram-bot/swagger-spring-1.0.0.jar"]