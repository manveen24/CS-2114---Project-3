/**
 * The kinds of help a student can ask for. Each category has a fixed
 * priority level, so the rule for who is served first is decided by the
 * category the student picks, not by anyone's judgment in the moment.
 *
 * Priority categories are served before regular categories. Requests in
 * the same level keep FIFO order.
 *
 * @author Luke
 * @version 2026.11.16
 */
public enum HelpCategory {
    /** Work due today or preparing for an exam. Priority. */
    DUE_TODAY("Due today / exam prep", true),

    /** A question expected to take under five minutes. Priority. */
    QUICK_QUESTION("Quick question (under 5 minutes)", true),

    /** Help finding or fixing a bug. Regular. */
    DEBUGGING("Debugging help", false),

    /** Help understanding a concept. Regular. */
    CONCEPT("Concept help", false);

    private final String label;
    private final boolean priority;

    /**
     * Creates a category.
     *
     * @param label
     *            the text shown to students
     * @param priority
     *            true if requests in this category are served first
     */
    HelpCategory(String label, boolean priority) {
        this.label = label;
        this.priority = priority;
    }


    /**
     * Returns the text shown to students for this category.
     *
     * @return the display label
     */
    public String getLabel() {
        return label;
    }


    /**
     * Reports whether requests in this category are served first.
     *
     * @return true for a priority category
     */
    public boolean isPriority() {
        return priority;
    }


    /**
     * Converts a one-based menu choice into a category. Surrounding spaces
     * are ignored.
     *
     * @param choice
     *            the menu choice, such as "1"
     * @return the matching category
     * @throws IllegalArgumentException
     *             if the choice is null or not a listed number
     */
    public static HelpCategory fromMenuChoice(String choice) {
        if (choice == null) {
            throw new IllegalArgumentException("Category cannot be null.");
        }

        HelpCategory[] categories = values();
        String trimmed = choice.trim();

        for (int i = 0; i < categories.length; i++) {
            if (trimmed.equals(String.valueOf(i + 1))) {
                return categories[i];
            }
        }

        throw new IllegalArgumentException(
            "Category must be a number from 1 to " + categories.length + ".");
    }
}
