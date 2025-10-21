FROM maven:3.9-eclipse-temurin-24 AS build
WORKDIR /workspace

COPY pom.xml ./
RUN mvn -B -q -e -N install

COPY common-dto ./common-dto
WORKDIR /workspace/common-dto
RUN mvn -B -q -e -DskipTests clean install

WORKDIR /workspace/CatService
COPY CatService/pom.xml ./
RUN mvn -B -q -e -DskipTests dependency:go-offline
COPY CatService/src ./src
RUN mvn -B -q -e -DskipTests package

FROM eclipse-temurin:24-jre
WORKDIR /app
COPY --from=build /workspace/CatService/target/*.jar /app/app.jar
EXPOSE 8081
ENTRYPOINT ["java","-jar","/app/app.jar"]
