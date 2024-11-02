package dev.markodojkic.softwaredevelopmentsimulation.test;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
        SwaggerTest.class,
        BaseTaskTest.class,
        DataProviderTest.class,
        DeveloperTest.class,
        DevelopmentTeamCreationParametersTest.class,
        DevelopersPageControllerTest.class,
        MainControllerTests.class,
        SoftwareDevelopmentSimulationAppTest.class
})
public class SoftwareDevelopmentSimulationAppTestsSuite {
    // This class remains empty. It is used only as a holder for the above annotations.
}
