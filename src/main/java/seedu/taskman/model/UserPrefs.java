package seedu.taskman.model;

import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;
import seedu.taskman.commons.core.GuiSettings;

import java.util.Objects;

/**
 * Represents User's preferences.
 */
public class UserPrefs {

    public GuiSettings guiSettings;

    public GuiSettings getGuiSettings() {
        return guiSettings == null ? new GuiSettings() : guiSettings;
    }

    public void updateLastUsedGuiSetting(GuiSettings guiSettings) {
        this.guiSettings = guiSettings;
    }

    public UserPrefs() {
        Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
        this.setGuiSettings((primaryScreenBounds.getWidth() / 2.5),
                            (primaryScreenBounds.getHeight()),
                            (int) (0.6*primaryScreenBounds.getWidth()),
                            0);
    }

    public static UserPrefs getUserPrefsForNonGuiTest() {
        return new UserPrefs(true);
    }

    // hack for tests only
    private UserPrefs (boolean ignored) {
        this.setGuiSettings(500, 500, 0, 0);
    }

    public void setGuiSettings(double width, double height, int x, int y) {
        guiSettings = new GuiSettings(width, height, x, y);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof UserPrefs)) { //this handles null as well.
            return false;
        }

        UserPrefs o = (UserPrefs) other;

        return Objects.equals(guiSettings, o.guiSettings);
    }

    @Override
    public int hashCode() {
        return Objects.hash(guiSettings);
    }

    @Override
    public String toString() {
        return guiSettings.toString();
    }

}
