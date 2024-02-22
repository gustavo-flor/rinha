FROM container-registry.oracle.com/graalvm/native-image:17-ol8 AS build

COPY .mvn .mvn
COPY mvnw .
COPY mvnw.cmd .

RUN ./mvnw --version

COPY pom.xml .
COPY src src

RUN gu install native-image;

RUN native-image --version

RUN ./mvnw --no-transfer-progress native:compile -Pnative -DskipTests

FROM container-registry.oracle.com/os/oraclelinux:8-slim AS release

COPY --from=build "/target/rinha" rinha

CMD [ "sh", "-c", "./rinha" ]