FROM ghcr.io/graalvm/jdk:22.3.2 AS build

# Update package lists and Install Maven
RUN microdnf update -y && \
    microdnf install -y maven gcc glibc-devel zlib-devel libstdc++-devel gcc-c++ && \
    microdnf clean all

WORKDIR /usr/src/app

# Copy pom.xml and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy src and build native image
COPY . .
RUN mvn -Pnative native:compile -DskipTests

# Second stage: Lightweight debian-slim image
FROM debian:bookworm-slim AS release

WORKDIR /app

# Copy the native binary from the build stage
COPY --from=build /usr/src/app/target/rinha /app/rinha

# Run the application
CMD ["/app/rinha"]