# Stage 1: Build the application using JDK 21
FROM eclipse-temurin:21-jdk AS build

# Set the working directory inside the container
WORKDIR /app

# Copy Maven wrapper and POM file for dependency resolution
COPY mvnw pom.xml ./
COPY .mvn .mvn

# Download dependencies to cache them and speed up subsequent builds
RUN ./mvnw dependency:go-offline

# Copy the entire source code and build the application
COPY src src
RUN ./mvnw clean package -DskipTests

# Stage 2: Create a lightweight runtime image with only JRE 21
FROM eclipse-temurin:21-jre

# Set the working directory for the application
WORKDIR /app

# Copy the built JAR file from the build stage
COPY --from=build /app/target/*.jar app.jar

# Copy resource files to ensure they are accessible at runtime
COPY src/main/resources src/main/resources

# Run the application
CMD ["java", "-jar", "app.jar"]
