FROM eclipse-temurin:21-jdk-jammy AS build
WORKDIR /app

COPY gradlew .
COPY gradle gradle
COPY build.gradle.kts .
COPY settings.gradle.kts .

COPY src src
COPY config config

RUN ./gradlew clean build -x test

FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

COPY --from=build /app/build/libs/*-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]
