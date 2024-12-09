package dev.markodojkic.softwaredevelopmentsimulation.flow;

import dev.markodojkic.softwaredevelopmentsimulation.util.Utilities;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.StandardIntegrationFlow;
import org.springframework.integration.file.FileHeaders;
import org.springframework.integration.file.dsl.Files;
import org.springframework.integration.file.support.FileExistsMode;
import org.springframework.messaging.MessageChannel;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

@Configuration
public class FileHandlingFlow {
    @Bean
    public IntegrationFlow informationLogFileFlow(@Qualifier("information.logFile.input") MessageChannel logFileMessageChannel) {
        return configureLogFileFlow(logFileMessageChannel, "informationChannel.log");
    }

    @Bean
    public IntegrationFlow jiraActivityStreamLogFileFlow(@Qualifier("jiraActivityStream.logFile.input") MessageChannel logFileMessageChannel) {
        return configureLogFileFlow(logFileMessageChannel, "jiraActivityStreamChannel.log");
    }

    @Bean
    public IntegrationFlow errorLogFileFlow(@Qualifier("errorChannel.logFile.input") MessageChannel logFileMessageChannel) {
        return configureLogFileFlow(logFileMessageChannel, "errorChannel.log");
    }

    private StandardIntegrationFlow configureLogFileFlow(MessageChannel logFileMessageChannel, String fileName) {
        return IntegrationFlow.from(logFileMessageChannel)
                .enrichHeaders(h -> h
                        .header(FileHeaders.FILENAME, fileName)
                        .header(FileHeaders.REMOTE_DIRECTORY, new File(Utilities.getCurrentApplicationLogsPath().toUri())))
                .transform(String.class, message -> message.concat("%$"))
                .handle(Files.outboundGateway(m -> m.getHeaders().get(FileHeaders.REMOTE_DIRECTORY))
                        .fileNameGenerator(m -> Objects.requireNonNull(m.getHeaders().get(FileHeaders.FILENAME)).toString())
                        .autoCreateDirectory(true)
                        .flushInterval(1000)
                        .fileExistsMode(FileExistsMode.APPEND_NO_FLUSH)
                        .appendNewLine(true)
                        .charset(StandardCharsets.UTF_8.name()))
                .get();
    }
}