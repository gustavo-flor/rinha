package com.github.gustavoflor.rinha.container;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

public class RedisContainer<SELF extends RedisContainer<SELF>> extends GenericContainer<SELF> {
    private static final int REDIS_PORT = 6379;

    public RedisContainer(String dockerImageName) {
        super(DockerImageName.parse(dockerImageName));
        addExposedPort(REDIS_PORT);
    }

    public int getPort() {
        return getMappedPort(REDIS_PORT);
    }
}
