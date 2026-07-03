FROM maven:3.9.9-eclipse-temurin-21

WORKDIR /workspace

COPY pom.xml ./
COPY src ./src

RUN mvn -q -DskipTests package

EXPOSE 8080

CMD ["sh", "-c", "java -jar target/sports-betting-settlement-service-0.0.1-SNAPSHOT.jar"]

