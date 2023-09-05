# Use the official Maven with JDK 17 image as the build image
FROM maven:3.9.3 AS build
# Set the current working directory inside the container
WORKDIR /app

# Copy the pom.xml file into our app directory
COPY ./pom.xml ./

# Download project dependencies
RUN mvn dependency:go-offline -B

# Copy the rest of the application source code
COPY ./src ./src

# Build the application
RUN mvn package -DskipTests

# Start with a base image containing Java runtime (JRE)
FROM eclipse-temurin:17-jdk-alpine

# Add a volume pointing to /tmp (optional)
VOLUME /tmp

# Make port 8080 available to the world outside this container
EXPOSE 8080

# Set application's JAR file
COPY --from=build /app/target/*.jar app.jar

# Set the entrypoint script as the entrypoint for the container
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]