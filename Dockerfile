FROM ghcr.io/graalvm/graalvm-ce:ol7-java17 AS build

ADD . /build
WORKDIR /build

# For SDKMAN to work we need unzip & zip
RUN yum install -y unzip zip

RUN \
    # Install SDKMAN
    curl -s "https://get.sdkman.io" | bash; \
    source "$HOME/.sdkman/bin/sdkman-init.sh"; \
    # Install Maven
    sdk install maven; \
    # Install GraalVM Native Image
    gu install native-image;

RUN source "$HOME/.sdkman/bin/sdkman-init.sh" && mvn --version

RUN native-image --version

RUN source "$HOME/.sdkman/bin/sdkman-init.sh" && mvn -Pnative native:compile -DskipTests


FROM oraclelinux:7-slim AS release

# Add Spring Boot Native app spring-boot-graal to Container
COPY --from=build "/build/target/rinha" rinha

CMD [ "sh", "-c", "./rinha" ]