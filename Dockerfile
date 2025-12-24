FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app


# copy pom.xml and dowload dependencies
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy source code and build
COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

#Copy the JAR from the build stage
COPY --from=build /app/target/*.jar app.jar

#Create directory for uploads images
RUN mkdir uploads

#Expose port
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
