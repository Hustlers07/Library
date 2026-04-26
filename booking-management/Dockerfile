# Use a lightweight JDK base image
FROM eclipse-temurin:17

# Set working directory
WORKDIR /app

# Copy the jar file into the container
COPY target/booking-management-app.jar app.jar

# Run the jar
ENTRYPOINT ["java","-jar","app.jar"]
