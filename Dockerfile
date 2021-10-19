FROM openjdk:11

ARG NAME=name
ARG TOKEN=token

RUN mkdir "/usr/app" mkdir "/usr/app/config" && mkdir "/usr/app/logs"
WORKDIR /usr/app

COPY gradle /usr/app/gradle
COPY src /usr/app/src
COPY build.gradle /usr/app
COPY gradlew /usr/app
COPY settings.gradle /usr/app
COPY src/main/resources/application.yaml /usr/app/config
RUN cd /usr/app && chmod +x gradlew && ./gradlew build

ENTRYPOINT ["java", "-jar", "/usr/app/build/libs/carbot-0.0.1.jar",\
    "--spring.config.location=/usr/app/config/*/",\
    "--cyprus.car.bot.name=${NAME}",\
    "--cyprus.car.bot.token=${TOKEN}"]