FROM ghcr.io/graalvm/graalvm-community:17 AS build

COPY pom.xml .

RUN mvn dependency:go-offline

COPY src src

RUN mvn -Pnative -Pproduction native:compile -DskipTests

FROM debian:bookworm-slim AS release

COPY --from=build /target/rinha /rinha

CMD ["/rinha"]