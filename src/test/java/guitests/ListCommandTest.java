package guitests;

import org.junit.Test;
import seedu.taskman.commons.core.Messages;
import seedu.taskman.testutil.TestTask;

import static org.junit.Assert.assertTrue;

public class ListCommandTest extends TaskManGuiTest {

    //@Test
    public void list_nonEmptyList() {
        assertListResult("list Mark"); //no results
        assertListResult("list Project", td.taskCS2103T, td.taskCS3244); //multiple results

        //list after deleting one result
        commandBox.runCommand("delete 1");
        assertListResult("list Project",td.taskCS3244);
    }

    //@Test
    public void list_emptyList(){
        commandBox.runCommand("clear");
        assertListResult("list IS1103"); //no results
    }

    //@Test
    public void list_invalidCommand_fail() {
        commandBox.runCommand("listBLAH");
        assertResultMessage(Messages.MESSAGE_UNKNOWN_COMMAND);
    }

    private void assertListResult(String command, TestTask... expectedHits ) {
        commandBox.runCommand(command);
        assertListSize(expectedHits.length);
        assertResultMessage(expectedHits.length + " tasks listed!");
        assertTrue(taskListPanel.isListMatching(expectedHits));
    }
}
