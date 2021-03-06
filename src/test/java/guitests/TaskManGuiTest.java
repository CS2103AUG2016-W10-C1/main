package guitests;

import guitests.guihandles.*;
import javafx.stage.Stage;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.rules.TestName;
import org.testfx.api.FxToolkit;
import seedu.taskman.TestApp;
import seedu.taskman.commons.core.EventsCenter;
import seedu.taskman.model.TaskMan;
import seedu.taskman.model.event.Activity;
import seedu.taskman.testutil.TestUtil;
import seedu.taskman.testutil.TypicalTestTasks;

import java.util.concurrent.TimeoutException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * A GUI Test class for TaskMan.
 */
public abstract class TaskManGuiTest {

    /* The TestName Rule makes the current test name available inside test methods */
    @Rule
    public TestName name = new TestName();

    TestApp testApp;

    protected TypicalTestTasks testTasks = new TypicalTestTasks();

    /*
     *   Handles to GUI elements present at the start up are created in advance
     *   for easy access from child classes.
     */
    protected MainGuiHandle mainGui;
    protected MainMenuHandle mainMenu;
    protected ListPanelHandle deadlineListPanel;
    protected ListPanelHandle floatingListPanel;
    protected ListPanelHandle scheduleListPanel;
    protected ResultDisplayHandle resultDisplay;
    protected CommandBoxHandle commandBox;
    private Stage stage;

    @BeforeClass
    public static void setupSpec() {
        try {
            FxToolkit.registerPrimaryStage();
            FxToolkit.hideStage();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }

    @Before
    public void setup() throws Exception {
        FxToolkit.setupStage((stage) -> {
            mainGui = new MainGuiHandle(new GuiRobot(), stage);
            mainMenu = mainGui.getMainMenu();
            deadlineListPanel = mainGui.getDeadlineListPanel();
            floatingListPanel = mainGui.getFloatingListPanel();
            scheduleListPanel = mainGui.getScheduleListPanel();
            resultDisplay = mainGui.getResultDisplay();
            commandBox = mainGui.getCommandBox();
            this.stage = stage;
        });
        EventsCenter.clearSubscribers();
        testApp = (TestApp) FxToolkit.setupApplication(() -> new TestApp(this::getInitialData, getDataFileLocation()));
        FxToolkit.showStage();
        while (!stage.isShowing()) ;
        mainGui.focusOnMainApp();
    }

    /**
     * Override this in child classes to set the initial LOCAL data.
     * Return null to use the data in the file specified in {@link #getDataFileLocation()}
     */
    protected TaskMan getInitialData() {
        TaskMan ab = TestUtil.generateEmptyTaskMan();
        testTasks.loadTaskManWithSampleData(ab);
        return ab;
    }

    /**
     * Override this in child classes to set the data file location.
     */
    protected String getDataFileLocation() {
        return TestApp.SAVE_LOCATION_FOR_TESTING;
    }

    @After
    public void cleanup() throws TimeoutException {
        FxToolkit.cleanupStages();
    }

    /**
     * Asserts the task shown in the row is same as the given task
     */
    public void assertMatching(Activity task, TaskRowHandle row) {
        assertTrue(TestUtil.compareRowAndTask(row, task));
    }

    /**
     * Asserts the size of the task list is equal to the given number.
     */
    protected void assertDeadlineListSize(int size) {
        int numberOfPeople = deadlineListPanel.getNumberOfPeople();
        assertEquals(size, numberOfPeople);
    }

    /**
     * Asserts the message shown in the Result Display area is same as the given string.
     */
    protected void assertResultMessage(String expected) {
        assertEquals(expected, resultDisplay.getText());
    }
}
