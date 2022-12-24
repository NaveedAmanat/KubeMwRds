FROM openjdk:8-jdk-alpine
MAINTAINER NaveedAmanat
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} rds.jar
ENTRYPOINT ["java","-jar","/rds.jar"]
EXPOSE 8383