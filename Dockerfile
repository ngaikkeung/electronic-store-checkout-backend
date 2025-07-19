# Use an official Maven image with OpenJDK 21 to build the application
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Use a lightweight OpenJDK 21 image to run the application
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
COPY src/main/resources/application.yaml ./application.yaml
COPY src/main/resources/data.sql ./data.sql
EXPOSE 8080
ENTRYPOINT ["java", "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005", "-jar", "app.jar", "--spring.config.location=classpath:/application.yaml"] 