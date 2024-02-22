FROM ghcr.io/graalvm/graalvm-ce:ol7-java17 AS build

COPY .mvn .mvn
COPY mvnw .
COPY mvnw.cmd .

RUN ./mvnw --version

COPY pom.xml .
COPY src src

RUN gu install native-image;

RUN native-image --version

RUN ./mvnw -Pnative native:compile -DskipTests

FROM oraclelinux:7-slim AS release

COPY --from=build "/target/rinha" rinha

CMD [ "sh", "-c", "./rinha" ]