# Build stage
FROM maven:3.9.5-amazoncorretto-17-debian AS build
COPY pom.xml /app/
COPY src /app/src
RUN mvn -f /app/pom.xml clean package

# Run stage
FROM openjdk:17-jdk-alpine
# Copy the jar to the container
COPY --from=build /app/target/CrimeXPlorer-0.0.1-SNAPSHOT.jar CrimeXPlorer-0.0.1-SNAPSHOT.jar
ENTRYPOINT [ "java", "-jar", "/CrimeXPlorer-0.0.1-SNAPSHOT.jar" ]