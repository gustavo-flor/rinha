FROM ghcr.io/graalvm/graalvm-ce:ol7-java17@sha256:98ec043d265387cb4e09cf6e548f182d42651e8c0b0792b91cf50872d0fc0b23 AS build

COPY .mvn .mvn
COPY mvnw .
COPY mvnw.cmd .

RUN ./mvnw --version

COPY pom.xml .
COPY src src

RUN gu install native-image;

RUN native-image --version

RUN ./mvnw clean -Pnative native:compile -DskipTests

FROM oraclelinux:7-slim AS release

COPY --from=build "/target/rinha" rinha

CMD [ "sh", "-c", "./rinha" ]