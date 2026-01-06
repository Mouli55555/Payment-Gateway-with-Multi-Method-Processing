FROM maven:3.9.6-eclipse-temurin-17 AS build

WORKDIR /app

# Copy pom and source together
COPY pom.xml .
COPY src ./src

# Build directly
RUN mvn -B clean package -DskipTests

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8000
ENTRYPOINT ["java","-jar","/app/app.jar"]
