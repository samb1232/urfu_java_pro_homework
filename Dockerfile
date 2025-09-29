FROM maven:3.9-eclipse-temurin-24 AS build
WORKDIR /workspace

COPY pom.xml ./
RUN mvn -B -q -e -DskipTests dependency:go-offline

COPY src ./src
RUN mvn -B -q -e -DskipTests package


FROM eclipse-temurin:24-jre
WORKDIR /app

COPY --from=build /workspace/target/*.jar /app/app.jar

EXPOSE 8080

# Environment variables can override Spring properties
# e.g., BOT_TOKEN maps to bot.token; SPRING_DATASOURCE_URL to spring.datasource.url
ENTRYPOINT ["java","-jar","/app/app.jar"]


