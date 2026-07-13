# Этап 1: Сборка приложения
FROM eclipse-temurin:21-jdk-jammy AS build
WORKDIR /app

# Копируем файлы сборщика
COPY gradlew .
COPY gradle gradle
COPY build.gradle.kts .
COPY settings.gradle.kts .

# Копируем исходный код и конфиг линтера
COPY src src
COPY config config

# Собираем jar-ник (пропуская тесты для экономии ресурсов Render)
RUN ./gradlew clean build -x test

# Этап 2: Запуск приложения
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# Копируем собранный jar из первого этапа
COPY --from=build /app/build/libs/app-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

# Запускаем приложение с ограничением памяти под бесплатный тариф Render (512MB)
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]
