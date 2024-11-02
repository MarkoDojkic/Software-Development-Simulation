package dev.markodojkic.softwaredevelopmentsimulation.test;

import dev.markodojkic.softwaredevelopmentsimulation.enums.DeveloperType;
import dev.markodojkic.softwaredevelopmentsimulation.model.Developer;
import dev.markodojkic.softwaredevelopmentsimulation.test.Config.SoftwareDevelopmentSimulationAppBaseTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DeveloperTest extends SoftwareDevelopmentSimulationAppBaseTest {
    private Developer developer;

    @BeforeEach
    @Override
    public void setup() {
        developer = new Developer();
    }

    @Test
    void when_noArgsConstructorIsCalled_correctValuesAreSetAsDefault() {
        assertNotNull(developer);
        assertNull(developer.getId());
        assertNull(developer.getName());
        assertNull(developer.getSurname());
        assertNull(developer.getYugoslavianUMCN());
        assertNull(developer.getPlaceOfBirth());
        assertEquals(DeveloperType.INTERN_DEVELOPER, developer.getDeveloperType());
        assertEquals("Intern developer", developer.getDeveloperType().getDisplayName());
        assertEquals(0L, developer.getExperienceCoefficient());
    }

    @Test
    void when_allArgsConstructorIsCalled_correctValuesAreSet() {
        Developer developerWithArgs = new Developer("John Doe", "1234567890123", DeveloperType.SENIOR_DEVELOPER, false, 2L);

        assertNotNull(developerWithArgs.getId());
        assertEquals("John", developerWithArgs.getName());
        assertEquals("Doe", developerWithArgs.getSurname());
        assertEquals("1234567890123", developerWithArgs.getYugoslavianUMCN());
        assertEquals("Serbian province of Vojvodina, Sremska Mitrovica region", developerWithArgs.getPlaceOfBirth());
        assertEquals(DeveloperType.SENIOR_DEVELOPER, developerWithArgs.getDeveloperType());
        assertEquals(2L, developerWithArgs.getExperienceCoefficient());
    }

    @Test
    void testGetDisplayName() {
        developer.setName("Alice");
        developer.setSurname("Johnson");

        assertEquals("Alice Johnson", developer.getDisplayName());
    }

    @Test
    void testToString() {
        developer = new Developer("123", "Bob", "Smith", "9876543210987", "Test area", DeveloperType.INTERN_DEVELOPER, 1L, false);
        String expectedString = "Developer(id=123, name=Bob, surname=Smith, yugoslavianUMCN=9876543210987, placeOfBirth=Test area, developerType=INTERN_DEVELOPER, experienceCoefficient=1)";

        assertEquals(expectedString, developer.toString());
    }

    @Test
    void when_equalsOrHashCodeIsCalled_onEqualObjectAreSame_onNonEqualObjectsAreDifferent() {
        Developer developer1 = new Developer("John Doe", "1234567858123", DeveloperType.SENIOR_DEVELOPER, false, 2L);
        Developer developer2 = new Developer("John Doe", "1234567858123", DeveloperType.SENIOR_DEVELOPER, false, 2L);
        Developer developer3 = new Developer("Alice Johnson", "9876543210987", DeveloperType.INTERN_DEVELOPER, true, 1L);

        assertEquals(developer1, developer2);
        assertEquals(developer1.hashCode(), developer2.hashCode());

        assertNotEquals(developer1, developer3);
        assertNotEquals(developer1.hashCode(), developer3.hashCode());

        assertNotEquals("John Doe", developer1);
    }
}