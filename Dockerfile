FROM eclipse-temurin:21-jre
WORKDIR /app
COPY target/smartparking-system-0.0.1-SNAPSHOT.jar /app/app.jar

CMD ["java", "-jar", "/app/app.jar"]
