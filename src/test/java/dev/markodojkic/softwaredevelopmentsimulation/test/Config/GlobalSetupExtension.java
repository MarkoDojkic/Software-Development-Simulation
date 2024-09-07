package dev.markodojkic.softwaredevelopmentsimulation.test.Config;

import dev.markodojkic.softwaredevelopmentsimulation.util.Utilities;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import static dev.markodojkic.softwaredevelopmentsimulation.util.DataProvider.setupDataProvider;

public class GlobalSetupExtension implements BeforeAllCallback, AfterAllCallback {
    @Override
    public void beforeAll(ExtensionContext extensionContext) {
        setupDataProvider();
    }

    @Override
    public void afterAll(ExtensionContext context) throws Exception {
        FileUtils.deleteDirectory(Utilities.getCurrentApplicationDataPath().toAbsolutePath().toFile());
    }
}