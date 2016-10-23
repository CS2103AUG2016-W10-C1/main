package seedu.taskman.logic.logicmanager;

import org.junit.Test;
import seedu.taskman.model.TaskMan;
import seedu.taskman.model.event.Activity;
import seedu.taskman.model.event.Task;

import java.util.List;

public class DeleteTests extends LogicManagerTestBase {

    @Test
    public void execute_deleteInvalidArgsFormat_errorMessageShown() throws Exception {
        assertIncorrectIndexFormatBehaviorForCommand("delete");
    }

    @Test
    public void execute_deleteIndexNotFound_errorMessageShown() throws Exception {
        assertIndexNotFoundBehaviorForCommand("delete");
    }

    @Test
    public void execute_delete_removesCorrectTask() throws Exception {
        TestDataHelper helper = new TestDataHelper();
        List<Task> threeTasks = helper.generateTaskList(3);

        TaskMan expectedTaskMan = helper.generateTaskMan(threeTasks);
        // TODO: overload method to accept tasks?
        expectedTaskMan.removeActivity(new Activity(threeTasks.get(1)));
        helper.addToModel(model, threeTasks);

        assertCommandStateChange("delete 2",
                expectedTaskMan,
                expectedTaskMan.getActivityList());
    }

}
