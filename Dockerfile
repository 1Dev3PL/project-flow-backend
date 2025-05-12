FROM maven:3.9.9-amazoncorretto-21-alpine AS build
COPY . /app
WORKDIR /app
RUN mvn clean package -Dmaven.test.skip

FROM openjdk:21-jdk-slim
COPY --from=build /app/target/*.jar /app.jar
EXPOSE 8080
CMD ["java", "-jar", "/app.jar"]