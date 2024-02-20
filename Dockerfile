FROM ghcr.io/graalvm/jdk:22.3.2 AS build

RUN microdnf update -y && \
  microdnf install -y maven gcc glibc-devel zlib-devel libstdc++-devel gcc-c++ && \
  microdnf clean all

COPY pom.xml .
COPY src src

RUN mvn -Pnative native:compile -DskipTests

FROM debian:bookworm-slim AS release

COPY --from=build /target/rinha /rinha

CMD ["/rinha"]