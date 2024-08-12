package dev.markodojkic.softwaredevelopmentsimulation.test;

import dev.markodojkic.softwaredevelopmentsimulation.enums.Priority;
import dev.markodojkic.softwaredevelopmentsimulation.enums.DeveloperType;
import dev.markodojkic.softwaredevelopmentsimulation.model.BaseTask;
import dev.markodojkic.softwaredevelopmentsimulation.model.Developer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;

import static dev.markodojkic.softwaredevelopmentsimulation.util.DataProvider.setupDataProvider;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class BaseTaskTest {
    @BeforeAll
    public static void preSetup(){
        setupDataProvider(true);
    }

    @Test
    void when_noArgsConstructorIsCalled_correctValuesAreSetAsDefault() {
        BaseTask task = new BaseTask();
        assertNotNull(task);
        assertNull(task.getId());
        assertNull(task.getName());
        assertNull(task.getDescription());
        assertNull(task.getPriority());
        assertNull(task.getAssignee());
        assertNull(task.getReporter());
        assertNull(task.getCreatedOn());
    }

    @Test
    void when_allArgsConstructorIsCalled_correctValuesAreSet() {
        Priority priority = Priority.NORMAL;
        Developer assignee = mock(Developer.class);
        Developer reporter = mock(Developer.class);
        ZonedDateTime createdOn = ZonedDateTime.now();

        BaseTask task = new BaseTask("1", "Task Name", "Task Description", priority, assignee, reporter, createdOn);

        assertEquals("1", task.getId());
        assertEquals("Task Name", task.getName());
        assertEquals("Task Description", task.getDescription());
        assertEquals(priority, task.getPriority());
        assertEquals(assignee, task.getAssignee());
        assertEquals(reporter, task.getReporter());
        assertEquals(createdOn, task.getCreatedOn());
    }

    @Test
    void when_equalsOrHashCodeIsCalled_onEqualObjectAreSame_onNonEqualObjectsAreDifferent() {
        // Mock Developer objects
        Developer assignee1 = mock(Developer.class);
        Developer assignee2 = mock(Developer.class);
        Developer reporter1 = mock(Developer.class);
        Developer reporter2 = mock(Developer.class);

        // Set up mocked Developer objects
        when(assignee1.getDisplayName()).thenReturn("John Doe");
        when(assignee2.getDisplayName()).thenReturn("Jane Smith");
        when(reporter1.getDisplayName()).thenReturn("Alice Johnson");
        when(reporter2.getDisplayName()).thenReturn("Bob Brown");

        ZonedDateTime sameNow = ZonedDateTime.now();

        // Create tasks with identical attributes
        BaseTask task1 = new BaseTask("1", "Task Name", "Task Description", Priority.NORMAL, assignee1, reporter1, sameNow);
        BaseTask task2 = new BaseTask("1", "Task Name", "Task Description", Priority.NORMAL, assignee1, reporter1, sameNow);

        // Test equality
        assertEquals(task1, task2);
        assertEquals(task1.hashCode(), task2.hashCode());

        // Create tasks with different IDs
        BaseTask task3 = new BaseTask("2", "Task Name", "Task Description", Priority.NORMAL, assignee1, reporter1, ZonedDateTime.now());
        assertNotEquals(task1, task3);

        // Create tasks with different names
        BaseTask task4 = new BaseTask("1", "Different Name", "Task Description", Priority.NORMAL, assignee1, reporter1, ZonedDateTime.now());
        assertNotEquals(task1, task4);

        // Create tasks with different priorities
        BaseTask task5 = new BaseTask("1", "Task Name", "Task Description", Priority.CRITICAL, assignee1, reporter1, ZonedDateTime.now());
        assertNotEquals(task1, task5);

        // Create tasks with different assignees
        BaseTask task6 = new BaseTask("1", "Task Name", "Task Description", Priority.NORMAL, assignee2, reporter1, ZonedDateTime.now());
        assertNotEquals(task1, task6);

        // Create tasks with different reporters
        BaseTask task7 = new BaseTask("1", "Task Name", "Task Description", Priority.NORMAL, assignee1, reporter2, ZonedDateTime.now());
        assertNotEquals(task1, task7);

        // Test with null values
        BaseTask task8 = new BaseTask("1", null, "Task Description", Priority.NORMAL, null, reporter1, ZonedDateTime.now());
        BaseTask task9 = new BaseTask("1", "Task Name", null, Priority.NORMAL, assignee1, null, ZonedDateTime.now());
        assertNotEquals(task1, task8);
        assertNotEquals(task1, task9);

        // Test with null
        assertNotNull(task1);
    }
    
    @Test
    void testToString() {
        Priority priority = Priority.NORMAL;
        Developer assignee = mock(Developer.class);
        Developer reporter = mock(Developer.class);
        ZonedDateTime createdOn = ZonedDateTime.now();

        BaseTask task = new BaseTask("1", "Task Name", "Task Description", priority, assignee, reporter, createdOn);

        String expectedToString = "BaseTask{id='1', name='Task Name', description='Task Description', priority=" + priority +
                ", assignee='" + assignee.getDisplayName() + "', reporter='" + reporter.getDisplayName() +
                "', createdOn=" + createdOn + '}';

        assertEquals(expectedToString, task.toString());

        task.setAssignee(null);
        task.setReporter(null);

        expectedToString = "BaseTask{id='1', name='Task Name', description='Task Description', priority=" + priority +
                ", assignee='UNASSIGNED', reporter='UNASSIGNED', createdOn=" + createdOn + '}';

        assertEquals(expectedToString, task.toString());
    }

    @Test
    void when_gettersAndSettersAreCalled_valuesAreCorrectlyRetrievedOrSet() {
        // Create a BaseTask object
        BaseTask task = new BaseTask();

        // Set values using setter methods
        task.setId("1");
        task.setName("Task 1");
        task.setDescription("Description for Task 1");
        task.setPriority(Priority.CRITICAL);
        task.setAssignee(new Developer("assignee 1", "1112989675102", DeveloperType.INTERN_DEVELOPER, true, (long) 1.25));
        task.setReporter(new Developer("reporter 1", "1112989675103", DeveloperType.INTERN_DEVELOPER, false, (long) 1.25));
        task.setCreatedOn(ZonedDateTime.now());

        // Verify values using getter methods
        assertEquals("1", task.getId());
        assertEquals("Task 1", task.getName());
        assertEquals("Description for Task 1", task.getDescription());
        assertEquals(Priority.CRITICAL, task.getPriority());
        assertEquals("assignee 1", task.getAssignee().getDisplayName());
        assertEquals("reporter 1", task.getReporter().getDisplayName());
        assertNotNull(task.getCreatedOn());
    }
}