package seedu.taskman.model;

import javafx.collections.transformation.FilteredList;
import seedu.taskman.commons.core.LogsCenter;
import seedu.taskman.commons.core.UnmodifiableObservableList;
import seedu.taskman.commons.events.model.TaskManChangedEvent;
import seedu.taskman.commons.util.StringUtil;
import seedu.taskman.model.event.ReadOnlyTask;
import seedu.taskman.model.event.Task;
import seedu.taskman.model.event.UniqueTaskList;
import seedu.taskman.model.event.UniqueTaskList.TaskNotFoundException;
import seedu.taskman.commons.core.ComponentManager;

import java.util.Set;
import java.util.logging.Logger;

/**
 * Represents the in-memory model of the task man data.
 * All changes to any model should be synchronized.
 */
public class ModelManager extends ComponentManager implements Model {
    private static final Logger logger = LogsCenter.getLogger(ModelManager.class);

    private final TaskMan taskMan;
    private final FilteredList<Task> filteredTasks;
    // TODO: add filteredEvents list

    /**
     * Initializes a ModelManager with the given TaskMan
     * TaskMan and its variables should not be null
     */
    public ModelManager(TaskMan src, UserPrefs userPrefs) {
        super();
        assert src != null;
        assert userPrefs != null;

        logger.fine("Initializing with Task Man: " + src + " and user prefs " + userPrefs);

        taskMan = new TaskMan(src);
        filteredTasks = new FilteredList<>(taskMan.getTasks());
    }

    public ModelManager() {
        this(new TaskMan(), new UserPrefs());
    }

    public ModelManager(ReadOnlyTaskMan initialData, UserPrefs userPrefs) {
        taskMan = new TaskMan(initialData);
        filteredTasks = new FilteredList<>(taskMan.getTasks());
    }

    @Override
    public void resetData(ReadOnlyTaskMan newData) {
        taskMan.resetData(newData);
        indicateTaskManChanged();
    }

    public ReadOnlyTaskMan getTaskMan() {
        return taskMan;
    }

    /** Raises an event to indicate the model has changed */
    private void indicateTaskManChanged() {
        raise(new TaskManChangedEvent(taskMan));
    }

    @Override
    public synchronized void deleteTask(ReadOnlyTask target) throws TaskNotFoundException {
        taskMan.removeTask(target);
        indicateTaskManChanged();
    }

    @Override
    public synchronized void addTask(Task task) throws UniqueTaskList.DuplicateTaskException {
        taskMan.addTask(task);
        updateFilteredListToShowAll();
        indicateTaskManChanged();
    }

    //=========== Filtered Task List Accessors ===============================================================

    @Override
    public UnmodifiableObservableList<ReadOnlyTask> getFilteredTaskList() {
        return new UnmodifiableObservableList<>(filteredTasks);
    }

    @Override
    public void updateFilteredListToShowAll() {
        filteredTasks.setPredicate(null);
    }

    @Override
    public void updateFilteredTaskList(Set<String> keywords){
        updateFilteredTaskList(new PredicateExpression(new TitleQualifier(keywords)));
    }

    private void updateFilteredTaskList(Expression expression) {
        filteredTasks.setPredicate(expression::satisfies);
    }

    //========== Inner classes/interfaces used for filtering ==================================================

    interface Expression {
        boolean satisfies(ReadOnlyTask task);
        String toString();
    }

    private class PredicateExpression implements Expression {

        private final Qualifier qualifier;

        PredicateExpression(Qualifier qualifier) {
            this.qualifier = qualifier;
        }

        @Override
        public boolean satisfies(ReadOnlyTask task) {
            return qualifier.run(task);
        }

        @Override
        public String toString() {
            return qualifier.toString();
        }
    }

    interface Qualifier {
        boolean run(ReadOnlyTask task);
        String toString();
    }

    private class TitleQualifier implements Qualifier {
        private Set<String> titleKeyWords;

        TitleQualifier(Set<String> titleKeyWords) {
            this.titleKeyWords = titleKeyWords;
        }

        @Override
        public boolean run(ReadOnlyTask task) {
            return titleKeyWords.stream()
                    .filter(keyword -> StringUtil.containsIgnoreCase(task.getTitle().title, keyword))
                    .findAny()
                    .isPresent();
        }

        @Override
        public String toString() {
            return "title=" + String.join(", ", titleKeyWords);
        }
    }

}
