# ---- Build Stage ----
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY . .

ARG MODULE_NAME
RUN mvn -q -DskipTests package -pl ${MODULE_NAME} -am

# ---- Runtime Stage ----
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

ARG MODULE_NAME
COPY --from=build /app/${MODULE_NAME}/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
