FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn clean package -DskipTests -Dmaven.compiler.release=17

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/reservas-ms-resource-service.jar reservas-ms-resource-service.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "reservas-ms-resource-service.jar"]