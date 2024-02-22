FROM ghcr.io/graalvm/graalvm-community:17.0.9-ol7-20231024 AS build

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