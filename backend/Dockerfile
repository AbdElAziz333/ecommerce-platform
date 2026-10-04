# Build Stage

FROM gradle:8.14.3-jdk17 AS build
WORKDIR /home/gradle/project

ARG APP_NAME
ARG APP_PORT

COPY build.gradle settings.gradle gradle.properties ./
COPY gradle ./gradle

COPY discovery/build.gradle discovery/
COPY gateway/build.gradle gateway/
COPY product/build.gradle product/
COPY order/build.gradle order/

RUN gradle :${APP_NAME}:dependencies --no-daemon

COPY discovery/src discovery/src
COPY gateway/src gateway/src
COPY product/src product/src
COPY order/src order/src

RUN gradle :${APP_NAME}:clean :${APP_NAME}:bootJar --no-daemon --no-build-cache

RUN cp /home/gradle/project/${APP_NAME}/build/libs/app.jar /app.jar

# Runtime Stage

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

COPY --from=build /app.jar app.jar

EXPOSE ${APP_PORT}

ENTRYPOINT ["java", "-jar", "app.jar"]