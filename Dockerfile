FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

#Copy the JAR from the build stage
COPY --from=build /app/target/*.jar app.jar

#Create directory for uploads images
RUN mkdir uploads

#Expose port
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
