package dev.markodojkic.softwaredevelopmentsimulation.test;

import dev.markodojkic.softwaredevelopmentsimulation.model.DevelopmentTeamCreationParameters;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static dev.markodojkic.softwaredevelopmentsimulation.util.DataProvider.setupDataProvider;
import static org.junit.jupiter.api.Assertions.*;

class DevelopmentTeamCreationParametersTest {
    private DevelopmentTeamCreationParameters params;

    @BeforeAll
    public static void preSetup(){
        setupDataProvider(true);
    }

    @BeforeEach
    public void setup() {
        // Initialize the DevelopmentTeamCreationParameters instance before each test
        params = new DevelopmentTeamCreationParameters();
    }

    @Test
    void when_noArgsConstructorIsCalled_correctValuesAreSetAsDefault() {
        // Verify that no-args constructor initializes fields to default values
        assertNotNull(params);
        assertEquals(45, params.getFemaleDevelopersPercentage());
        assertEquals(30, params.getMinimalDevelopersCount());
        assertEquals(100, params.getMaximalDevelopersCount());
        assertEquals(5, params.getMinimalDevelopersInTeamCount());
        assertEquals(15, params.getMaximalDevelopersInTeamCount());
        assertTrue(params.isRetainOld());
    }

    @Test
    void when_allArgsConstructorIsCalled_correctValuesAreSet() {
        // Test all-args constructor
        DevelopmentTeamCreationParameters paramsWithArgs = new DevelopmentTeamCreationParameters(50, 20, 80, 3, 10, false);
        assertEquals(50, paramsWithArgs.getFemaleDevelopersPercentage());
        assertEquals(20, paramsWithArgs.getMinimalDevelopersCount());
        assertEquals(80, paramsWithArgs.getMaximalDevelopersCount());
        assertEquals(3, paramsWithArgs.getMinimalDevelopersInTeamCount());
        assertEquals(10, paramsWithArgs.getMaximalDevelopersInTeamCount());
        assertFalse(paramsWithArgs.isRetainOld());
    }

    @Test
    void when_gettersAndSettersAreCalled_valuesAreCorrectlyRetrievedOrSet() {
        // Test setters and getters
        params.setFemaleDevelopersPercentage(60);
        params.setMinimalDevelopersCount(25);
        params.setMaximalDevelopersCount(90);
        params.setMinimalDevelopersInTeamCount(2);
        params.setMaximalDevelopersInTeamCount(8);
        params.setRetainOld(false);

        assertEquals(60, params.getFemaleDevelopersPercentage());
        assertEquals(25, params.getMinimalDevelopersCount());
        assertEquals(90, params.getMaximalDevelopersCount());
        assertEquals(2, params.getMinimalDevelopersInTeamCount());
        assertEquals(8, params.getMaximalDevelopersInTeamCount());
        assertFalse(params.isRetainOld());
    }

    @Test
    void when_equalsOrHashCodeIsCalled_onEqualObjectAreSame_onNonEqualObjectsAreDifferent() {
        // Test equals() and hashCode()
        DevelopmentTeamCreationParameters params1 = new DevelopmentTeamCreationParameters(45, 30, 100, 5, 15, true);
        DevelopmentTeamCreationParameters params2 = new DevelopmentTeamCreationParameters(45, 30, 100, 5, 15, true);
        DevelopmentTeamCreationParameters params3 = new DevelopmentTeamCreationParameters(50, 30, 100, 5, 15, true);

        assertEquals(params1, params2);  // Objects with identical attributes should be equal
        assertEquals(params1.hashCode(), params2.hashCode());

        assertNotEquals(params1, params3);  // Objects with different attributes should not be equal
        assertNotEquals(params1.hashCode(), params3.hashCode());
    }
}