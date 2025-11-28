# ---------- Stage 1: build the Spring Boot jar ----------
FROM maven:3.9-eclipse-temurin-21 AS build

# Inside the image, work in /app
WORKDIR /app

# Copy pom.xml and source code into the image
COPY pom.xml .
COPY src ./src

# Build the project (creates target/*.jar)
RUN mvn -B clean package -DskipTests


# ---------- Stage 2: run the app ----------
FROM eclipse-temurin:21-jre

WORKDIR /app

# Copy jar from the build stage
COPY --from=build /app/target/*.jar app.jar

# Expose the port your app listens on
EXPOSE 8080

# Start the Spring Boot app
ENTRYPOINT ["java", "-jar", "app.jar"]
