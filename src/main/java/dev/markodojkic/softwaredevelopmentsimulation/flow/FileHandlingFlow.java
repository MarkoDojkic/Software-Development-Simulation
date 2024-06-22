package dev.markodojkic.softwaredevelopmentsimulation.flow;

import dev.markodojkic.softwaredevelopmentsimulation.util.Utilities;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.file.FileHeaders;
import org.springframework.integration.file.dsl.Files;
import org.springframework.integration.file.support.FileExistsMode;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

@Configuration
public class FileHandlingFlow {
    @Bean
    public IntegrationFlow informationLogFileFlow() {
        return configureLogFileFlow("information.logFile.input", "informationData.log");
    }

    @Bean
    public IntegrationFlow jiraActivityStreamLogFileFlow() {
        return configureLogFileFlow("jiraActivityStream.logFile.input", "jiraActivityStreamData.log");
    }

    @Bean
    public IntegrationFlow errorLogFileFlow() {
        return configureLogFileFlow("error.logFile.input", "errorData.log");
    }

    private IntegrationFlow configureLogFileFlow(String inputChannel, String fileName) {
        return IntegrationFlow.from(inputChannel)
                .enrichHeaders(h -> h
                        .header(FileHeaders.FILENAME, fileName)
                        .header(FileHeaders.REMOTE_DIRECTORY, new File(Utilities.getCurrentApplicationLogsPath().toUri())))
                .transform(String.class, message -> message.concat("%$"))
                .handle(Files.outboundGateway(m -> m.getHeaders().get(FileHeaders.REMOTE_DIRECTORY))
                        .fileNameGenerator(m -> Objects.requireNonNull(m.getHeaders().get(FileHeaders.FILENAME)).toString())
                        .autoCreateDirectory(true)
                        .flushInterval(5000)
                        .fileExistsMode(FileExistsMode.APPEND_NO_FLUSH)
                        .appendNewLine(true)
                        .charset(StandardCharsets.UTF_8.name()))
                .get();
    }
}
