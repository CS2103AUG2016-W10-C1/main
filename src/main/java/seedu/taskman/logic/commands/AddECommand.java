package seedu.taskman.logic.commands;

import seedu.taskman.commons.exceptions.IllegalValueException;
import seedu.taskman.logic.parser.CommandParser;
import seedu.taskman.model.tag.Tag;
import seedu.taskman.model.tag.UniqueTagList;
import seedu.taskman.model.event.*;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static seedu.taskman.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

/**
 * Adds an event to the task man.
 */
public class AddECommand extends Command {

    public static final String COMMAND_WORD = "adde";

    public static final String MESSAGE_USAGE = "Add an event to TaskMan.\n"
            + "Parameters: TITLE s/SCHEDULE [t/TAGS]...\n"
            + "Example: " + COMMAND_WORD
            + " star gazing s/today 2300, today 2330 t/telescope";

    public static final String MESSAGE_SUCCESS = "New event added: %1$s";
    public static final String MESSAGE_DUPLICATE_EVENT = "This event already exists in TaskMan";

    public static final String MESSAGE_ADDE_INVALID_COMMAND_FORMAT = MESSAGE_INVALID_COMMAND_FORMAT
            + "\n" + COMMAND_WORD + ": " + MESSAGE_USAGE;

    private static final Pattern EVENT_MARK_ARGS_FORMAT =
            Pattern.compile("" + CommandParser.ArgumentPattern.TITLE
                    + CommandParser.ArgumentPattern.SCHEDULE
                    + CommandParser.ArgumentPattern.OPTIONAL_TAGS);

    private final Event toAdd;

    /**
     * Convenience constructor using raw values.
     *
     * @throws IllegalValueException if any of the raw values are invalid
     */
    private AddECommand(String title, String schedule, Set<String> tags)
            throws IllegalValueException {
        super(true);
        final Set<Tag> tagSet = new HashSet<>();
        for (String tagName : tags) {
            tagSet.add(new Tag(tagName));
        }
        this.toAdd = new Event(
                new Title(title),
                new UniqueTagList(tagSet),
                new Schedule(schedule)
        );
    }

    public static Command prepareAddE(String args) {
        final Matcher matcher = EVENT_MARK_ARGS_FORMAT.matcher(args.trim());
        // Validate arg string format
        if (!matcher.matches()) {
            return new IncorrectCommand(MESSAGE_ADDE_INVALID_COMMAND_FORMAT);
        }
        try {
            return new AddECommand(
                    matcher.group(CommandParser.Group.title.name),
                    matcher.group(CommandParser.Group.schedule.name),
                    getTagsFromArgs(matcher.group(CommandParser.Group.tagArguments.name))
            );
        } catch (IllegalValueException ive) {
            return new IncorrectCommand(ive.getMessage());
        }
    }

    @Override
    public CommandResult execute() {
        assert model != null;
        try {
            model.addActivity(toAdd);
            return new CommandResult(String.format(MESSAGE_SUCCESS, toAdd), true);
        } catch (UniqueActivityList.DuplicateActivityException e) {
            return new CommandResult(MESSAGE_DUPLICATE_EVENT, false);
        }

    }

}