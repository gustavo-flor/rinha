package com.github.gustavoflor.rinha;

import com.github.gustavoflor.rinha.core.Customer;
import com.github.gustavoflor.rinha.core.Transfer;
import com.github.gustavoflor.rinha.core.usecase.statement.StatementUseCaseInput;
import com.github.gustavoflor.rinha.core.usecase.statement.StatementUseCaseOutput;
import com.github.gustavoflor.rinha.core.usecase.transfer.TransferUseCaseInput;
import com.github.gustavoflor.rinha.core.usecase.transfer.TransferUseCaseOutput;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
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
import org.springframework.context.annotation.ImportRuntimeHints;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import static org.springframework.aot.hint.MemberCategory.DECLARED_CLASSES;
import static org.springframework.aot.hint.MemberCategory.DECLARED_FIELDS;
import static org.springframework.aot.hint.MemberCategory.INVOKE_DECLARED_CONSTRUCTORS;
import static org.springframework.aot.hint.MemberCategory.INVOKE_DECLARED_METHODS;
import static org.springframework.aot.hint.MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS;
import static org.springframework.aot.hint.MemberCategory.INVOKE_PUBLIC_METHODS;

@SpringBootApplication(
	proxyBeanMethods = false,
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
		TomcatMetricsAutoConfiguration.class
	}
)
@EnableJpaRepositories(basePackages = "com.github.gustavoflor.rinha.core.repository")
@ImportRuntimeHints(Application.Hints.class)
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	public static class Hints implements RuntimeHintsRegistrar {
		@Override
		public void registerHints(final RuntimeHints hints, final ClassLoader classLoader) {
			hints.reflection()
				.registerType(PostgreSQLEnumJdbcType.class, INVOKE_DECLARED_CONSTRUCTORS)
				.registerType(StatementUseCaseInput.class, INVOKE_PUBLIC_METHODS)
				.registerType(TransferUseCaseInput.class, INVOKE_PUBLIC_METHODS)
				.registerType(StatementUseCaseOutput.class, INVOKE_DECLARED_CONSTRUCTORS, DECLARED_FIELDS, INVOKE_DECLARED_METHODS)
				.registerType(Customer.class, INVOKE_DECLARED_CONSTRUCTORS, DECLARED_FIELDS, INVOKE_DECLARED_METHODS)
				.registerType(Transfer.class, INVOKE_DECLARED_CONSTRUCTORS, DECLARED_FIELDS, INVOKE_DECLARED_METHODS);
		}
	}

}
