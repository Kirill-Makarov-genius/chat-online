# Используем легкий образ Java 21 на базе Alpine Linux
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# 1. Устанавливаем FFmpeg внутри контейнера
# --no-cache позволяет не хранить временные файлы установки, уменьшая размер образа
RUN apk add --no-cache ffmpeg

# 2. Создаем папку для загрузок (видео/изображения)
RUN mkdir -p /app/uploads && chmod 777 /app/uploads

# 3. Копируем ваш JAR-файл, который вы собрали локально (mvn package)
# Убедитесь, что вы запускаете 'docker build' в папке, где есть папка target
COPY target/*.jar app.jar

# Открываем порт
EXPOSE 8080

# Запуск приложения
ENTRYPOINT ["java", "-jar", "app.jar"]