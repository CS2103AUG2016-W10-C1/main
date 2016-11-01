package seedu.taskman.model;

import javafx.collections.ObservableList;
import seedu.taskman.model.event.Activity;
import seedu.taskman.model.event.Event;
import seedu.taskman.model.event.MutableTagsEvent;
import seedu.taskman.model.event.Task;
import seedu.taskman.model.event.UniqueActivityList;
import seedu.taskman.model.tag.Tag;
import seedu.taskman.model.tag.UniqueTagList;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Wraps all data at the task-man level
 * Duplicates are not allowed (by .equals comparison)
 */
public class TaskMan implements ReadOnlyTaskMan {

    private final UniqueActivityList activities;
    private final UniqueTagList tags;

    // TODO: format looks pretty weird. can we do something about it?
    {
        activities = new UniqueActivityList();
        tags = new UniqueTagList();
    }

    public TaskMan() {
    }

    /**
     * Tasks and Tags are copied into this taskMan
     */
    public TaskMan(ReadOnlyTaskMan toBeCopied) {
        this(toBeCopied.getUniqueActivityList(), toBeCopied.getUniqueTagList());
    }

    /**
     * Tasks and Tags are copied into this taskMan
     */
    public TaskMan(UniqueActivityList activities, UniqueTagList tags) {
        resetData(activities.getInternalList(), tags.getInternalList());
    }

    // TODO: Review - do we really need this?
    public static ReadOnlyTaskMan getEmptyTaskMan() {
        return new TaskMan();
    }

//// list overwrite operations

    public ObservableList<Activity> getActivities() {
        return activities.getInternalList();
    }

    public void setActivities(List<Activity> activities) {
        this.activities.getInternalList().setAll(activities);
    }

    // TODO: Create setEvent
    public ObservableList<Tag> getTags() {
        return tags.getInternalList();
    }
    
    public void setTags(Collection<Tag> tags) {
        this.tags.getInternalList().setAll(tags);
    }

    public void resetData(Collection<? extends Activity> newActivities, Collection<Tag> newTags) {
        setActivities(newActivities.stream().map(Activity::new).collect(Collectors.toList()));
        setTags(newTags);
    }

    public void resetData(ReadOnlyTaskMan newData) {
        resetData(newData.getActivityList(), newData.getTagList());
    }

//// event-level operations

    public void addActivity(Event event) throws UniqueActivityList.DuplicateActivityException {
        addActivity(new Activity(event));
    }

    /**
     * Adds an activity to TaskMan.
     * Also checks the new activity's tags and updates {@link #tags} with any new tags found,
     * and updates the Tag objects in the activity to point to those in {@link #tags}.
     *
     * @throws UniqueActivityList.DuplicateActivityException if an equivalent activity already exists.
     */
    public void addActivity(Activity activity) throws UniqueActivityList.DuplicateActivityException {
        syncTagsWithMasterList(activity);
        activities.add(activity);
    }

    /**
     * Ensures that every tag in this event:
     * - exists in the master list {@link #tags}
     * - points to a Tag object in the master list
     * TODO: feels like a pretty complex way to do this...
     * // can't we just store tags from eventTags into tagsList? Objects are passed by reference
     */
    private void syncTagsWithMasterList(MutableTagsEvent event) {
        final UniqueTagList eventTags = event.getTags();
        tags.mergeFrom(eventTags);

        // Create map with values = tag object references in the master list
        final Map<Tag, Tag> masterTagObjects = new HashMap<>();
        for (Tag tag : tags) {
            masterTagObjects.put(tag, tag);
        }

        // Rebuild the list of event tags using references from the master list
        final Set<Tag> commonTagReferences = new HashSet<>();
        for (Tag tag : eventTags) {
            commonTagReferences.add(masterTagObjects.get(tag));
        }
        event.setTags(new UniqueTagList(commonTagReferences));
    }

    public boolean removeActivity(Task key) throws UniqueActivityList.ActivityNotFoundException {
        if (activities.remove(new Activity(key))) {
            return true;
        } else {
            throw new UniqueActivityList.ActivityNotFoundException();
        }
    }

    public boolean removeActivity(Activity key) throws UniqueActivityList.ActivityNotFoundException {
        if (activities.remove(key)) {
            return true;
        } else {
            throw new UniqueActivityList.ActivityNotFoundException();
        }
    }

//// tag-level operations

    public void addTag(Tag t) throws UniqueTagList.DuplicateTagException {
        tags.add(t);
    }

//// util methods

    @Override
    public String toString() {
        return activities.getInternalList().size() + " activities, " + tags.getInternalList().size() + " tags";
    }

    @Override
    public List<Activity> getActivityList() {
        return Collections.unmodifiableList(activities.getInternalList());
    }

    @Override
    public List<Tag> getTagList() {
        return Collections.unmodifiableList(tags.getInternalList());
    }

    @Override
    public UniqueActivityList getUniqueActivityList() {
        return this.activities;
    }

    @Override
    public UniqueTagList getUniqueTagList() {
        return this.tags;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TaskMan taskMan = (TaskMan) o;
        return activities.equals(taskMan.activities) &&
                tags.equals(taskMan.tags);
    }

    @Override
    public int hashCode() {
        return Objects.hash(activities, tags);
    }
}
