# Use a slim JRE 21 image for the runtime
FROM eclipse-temurin:21-jre-alpine

# Create a non-root user for better security
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Standard Spring Boot temp directory volume
VOLUME /tmp

# Argument for the jar location
ARG JAR_FILE=target/*.jar

# Copy the jar with a descriptive name
COPY ${JAR_FILE} app.jar

# Optimization: Tweak memory settings for containers
# Using -XX:+UseContainerSupport (default in Java 21) allows Java to 
# respect the Docker memory limits set in docker-compose.
ENTRYPOINT ["java", "-Xmx512m", "-Xms256m", "-jar", "/app.jar"]