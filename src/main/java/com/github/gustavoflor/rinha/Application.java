package com.github.gustavoflor.rinha;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.endpoint.jmx.JmxEndpointAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.metrics.CompositeMeterRegistryAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.metrics.JvmMetricsAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.metrics.LogbackMetricsAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.metrics.SystemMetricsAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.metrics.export.simple.SimpleMetricsExportAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.metrics.jdbc.DataSourcePoolMetricsAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.metrics.startup.StartupTimeMetricsListenerAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.metrics.task.TaskExecutorMetricsAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.metrics.web.tomcat.TomcatMetricsAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.observation.ObservationAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.observation.web.client.HttpClientObservationsAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.scheduling.ScheduledTasksObservabilityAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.admin.SpringApplicationAdminJmxAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisReactiveAutoConfiguration;
import org.springframework.boot.autoconfigure.integration.IntegrationAutoConfiguration;
import org.springframework.boot.autoconfigure.jmx.JmxAutoConfiguration;
import org.springframework.boot.autoconfigure.netty.NettyAutoConfiguration;
import org.springframework.boot.autoconfigure.reactor.ReactorAutoConfiguration;
import org.springframework.boot.autoconfigure.ssl.SslAutoConfiguration;
import org.springframework.boot.autoconfigure.task.TaskExecutionAutoConfiguration;
import org.springframework.boot.autoconfigure.task.TaskSchedulingAutoConfiguration;
import org.springframework.boot.autoconfigure.web.client.RestClientAutoConfiguration;
import org.springframework.boot.autoconfigure.web.client.RestTemplateAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(
	scanBasePackages = "com.github.gustavoflor.rinha",
	exclude = {
		IntegrationAutoConfiguration.class,
		SslAutoConfiguration.class,
		ErrorMvcAutoConfiguration.class,
		JmxAutoConfiguration.class,
		NettyAutoConfiguration.class,
		ReactorAutoConfiguration.class,
		RedisReactiveAutoConfiguration.class,
		TaskExecutionAutoConfiguration.class,
		RestClientAutoConfiguration.class,
		RestTemplateAutoConfiguration.class,
		SpringApplicationAdminJmxAutoConfiguration.class,
		TaskSchedulingAutoConfiguration.class,
		HttpClientObservationsAutoConfiguration.class,
		JmxEndpointAutoConfiguration.class,
		ObservationAutoConfiguration.class,
		ScheduledTasksObservabilityAutoConfiguration.class,
		CompositeMeterRegistryAutoConfiguration.class,
		DataSourcePoolMetricsAutoConfiguration.class,
		JvmMetricsAutoConfiguration.class,
		LogbackMetricsAutoConfiguration.class,
		SimpleMetricsExportAutoConfiguration.class,
		StartupTimeMetricsListenerAutoConfiguration.class,
		SystemMetricsAutoConfiguration.class,
		TaskExecutorMetricsAutoConfiguration.class,
		TomcatMetricsAutoConfiguration.class,
	}
)
@EnableJpaRepositories(basePackages = "com.github.gustavoflor.rinha.core.repository")
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}
