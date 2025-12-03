package costa.paltrinieri.felipe.infrastructure.logging;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.ConsoleAppender;
import jakarta.annotation.PostConstruct;
import net.logstash.logback.encoder.LogstashEncoder;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class LoggingConfig {

    @Configuration
    @Profile("prod")
    static class ProdLoggingConfig {

        @PostConstruct
        public void configureJsonLogging() {
            LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
            Logger rootLogger = context.getLogger(Logger.ROOT_LOGGER_NAME);
            rootLogger.detachAndStopAllAppenders();

            ConsoleAppender<ILoggingEvent> appender = new ConsoleAppender<>();
            appender.setContext(context);
            appender.setName("JSON");

            LogstashEncoder encoder = new LogstashEncoder();
            encoder.setContext(context);
            encoder.start();

            appender.setEncoder(encoder);
            appender.start();

            rootLogger.addAppender(appender);
        }

    }

    @Configuration
    @Profile("dev")
    static class DevLoggingConfig {

        @PostConstruct
        public void configureTextLogging() {
            LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
            Logger rootLogger = context.getLogger(Logger.ROOT_LOGGER_NAME);
            rootLogger.detachAndStopAllAppenders();

            ConsoleAppender<ILoggingEvent> appender = new ConsoleAppender<>();
            appender.setContext(context);
            appender.setName("CONSOLE");

            PatternLayoutEncoder encoder = new PatternLayoutEncoder();
            encoder.setContext(context);
            encoder.setPattern("%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] [%X{correlationId}] %-5level %logger{36} - %msg%n");
            encoder.start();

            appender.setEncoder(encoder);
            appender.start();

            rootLogger.addAppender(appender);
        }

    }

}
