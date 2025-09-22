FROM openjdk:21 AS build

COPY build/libs/MicrometerDemo-1.0-SNAPSHOT-all.jar app.jar

EXPOSE 8081

ENTRYPOINT ["java","-jar","app.jar"]