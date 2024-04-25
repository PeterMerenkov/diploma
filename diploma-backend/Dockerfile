FROM maven:3.8.3-openjdk-17-slim

WORKDIR /app

COPY . .

RUN mvn package

EXPOSE 8081

CMD ["java", "-jar", "target/diploma-backend-0.0.1-SNAPSHOT.jar"]
