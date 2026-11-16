import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Model for an office hours waiting line. Owns the queues of help requests
 * and enforces session rules: requests are validated, student identifiers
 * are unique among active (waiting) requests, and served or canceled
 * students may join again.
 *
 * Requests are served by priority. A request's priority comes from the
 * HelpCategory the student picks when joining. Every priority request is
 * served before every regular request, and requests at the same priority
 * level keep FIFO order. Priority and regular requests are kept in two
 * separate LinkedQueues so that each level stays a plain FIFO queue.
 *
 * The model never reads input or prints output. It returns values or throws
 * exceptions, and the controller decides how to present them.
 *
 * @author Luke
 * @version 2026.11.16
 */
public class OfficeHoursModel {
    private static final int ESTIMATED_HELP_MINUTES = 30;

    private final LinkedQueue<HelpRequest> priority;
    private final LinkedQueue<HelpRequest> regular;

    /**
     * Creates a model with an empty waiting line.
     */
    public OfficeHoursModel() {
        priority = new LinkedQueue<>();
        regular = new LinkedQueue<>();
    }


    /**
     * Adds a request to the waiting line in the default category,
     * Concept help, which is regular priority.
     *
     * @param name
     *            the student's name
     * @param studentId
     *            the student's identifier
     * @param topic
     *            the help topic
     * @return the student's one-based position in service order
     * @throws IllegalArgumentException
     *             if any field is invalid; the line is left unchanged
     * @throws IllegalStateException
     *             if a request with the same normalized identifier is
     *             already waiting; the line is left unchanged
     */
    public int join(String name, String studentId, String topic) {
        return join(name, studentId, topic, HelpCategory.CONCEPT);
    }


    /**
     * Adds a request to the waiting line. The category decides its
     * priority level: a priority request goes behind earlier priority
     * requests but ahead of every regular request.
     *
     * @param name
     *            the student's name
     * @param studentId
     *            the student's identifier
     * @param topic
     *            the help topic
     * @param category
     *            the kind of help needed, which sets the priority level
     * @return the student's one-based position in service order
     * @throws IllegalArgumentException
     *             if any field is invalid or the category is null; the
     *             line is left unchanged
     * @throws IllegalStateException
     *             if a request with the same normalized identifier is
     *             already waiting; the line is left unchanged
     */
    public int join(
        String name,
        String studentId,
        String topic,
        HelpCategory category) {
        if (category == null) {
            throw new IllegalArgumentException("Category cannot be null.");
        }
        boolean isPriority = category.isPriority();

        // HelpRequest's constructor validates every field before we touch
        // either queue, so an invalid join cannot change state.
        HelpRequest request =
            new HelpRequest(name, studentId, topic, isPriority);
        if (isWaiting(request.getStudentId())) {
            throw new IllegalStateException(
                "A request with that identifier is already waiting.");
        }

        if (isPriority) {
            priority.enqueue(request);
            return priority.size();
        }
        regular.enqueue(request);
        return priority.size() + regular.size();
    }


    /**
     * Removes and returns the next student in service order: the oldest
     * priority request if there is one, otherwise the oldest regular
     * request.
     *
     * @return the request that was served
     * @throws NoSuchElementException
     *             if no students are waiting
     */
    public HelpRequest callNext() {
        if (!priority.isEmpty()) {
            return priority.dequeue();
        }
        return regular.dequeue();
    }


    /**
     * Removes the waiting request with the given identifier, keeping
     * everyone else in their original order.
     *
     * @param studentId
     *            the identifier to cancel (normalized before comparing)
     * @return true if a request was removed, false if no waiting request
     *         has that identifier
     * @throws IllegalArgumentException
     *             if the identifier is invalid; the line is left unchanged
     */
    public boolean cancel(String studentId) {
        String key = normalize(studentId);

        // Search snapshots first so an absent identifier never mutates
        // either queue.
        if (contains(priority, key)) {
            removeFrom(priority, key);
            return true;
        }
        if (contains(regular, key)) {
            removeFrom(regular, key);
            return true;
        }
        return false;
    }


    /**
     * Checks whether a request with the given identifier is waiting at
     * either priority level.
     *
     * @param studentId
     *            the identifier to look for (normalized before comparing)
     * @return true if a matching request is waiting
     * @throws IllegalArgumentException
     *             if the identifier is invalid
     */
    public boolean isWaiting(String studentId) {
        String key = normalize(studentId);
        return contains(priority, key) || contains(regular, key);
    }


    /**
     * Returns a snapshot of the waiting line in service order: priority
     * requests first, then regular requests, each in FIFO order. Changing
     * the returned list does not change the model. A shallow copy is
     * enough because HelpRequest objects are immutable.
     *
     * @return the waiting requests, next to be served first
     */
    public List<HelpRequest> getWaitingRequests() {
        List<HelpRequest> order = priority.toList();
        order.addAll(regular.toList());
        return order;
    }


    /**
     * Returns how many students are waiting at both priority levels.
     *
     * @return the number of waiting requests
     */
    public int getWaitingCount() {
        return priority.size() + regular.size();
    }


    /**
     * Returns how many priority requests are waiting.
     *
     * @return the number of waiting priority requests
     */
    public int getPriorityCount() {
        return priority.size();
    }


    /**
     * Calculates the estimated waiting time for a student based on their
     * position in service order.
     *
     * @param position
     *            the student's one-based position in service order
     * @return estimated waiting time in minutes
     * @throws IllegalArgumentException
     *             if the position is not in the line
     */
    public int getEstimatedWaitMinutes(int position) {
        if (position < 1 || position > getWaitingCount()) {
            throw new IllegalArgumentException(
                "Invalid waiting position.");
        }

        return (position - 1) * ESTIMATED_HELP_MINUTES;
    }


    /**
     * Returns the estimated waiting time for every student currently in
     * the line, in service order.
     *
     * @return list of estimated waiting times
     */
    public List<Integer> getEstimatedWaitTimes() {
        List<Integer> estimates = new ArrayList<>();

        for (int position = 1; position <= getWaitingCount(); position++) {
            estimates.add(getEstimatedWaitMinutes(position));
        }

        return estimates;
    }


    /**
     * Checks whether the waiting line is empty.
     *
     * @return true if no students are waiting
     */
    public boolean isEmpty() {
        return priority.isEmpty() && regular.isEmpty();
    }


    // ----------------------------------------------------------------------
    // Private helpers
    // ----------------------------------------------------------------------

    /**
     * Validates and normalizes an identifier using HelpRequest's shared
     * rule, so joins and cancellations compare identifiers the same way.
     */
    private String normalize(String studentId) {
        return HelpRequest.normalizeStudentId(studentId);
    }


    /**
     * Checks whether a queue holds a request matching a normalized key.
     */
    private boolean contains(LinkedQueue<HelpRequest> queue, String key) {
        for (HelpRequest request : queue.toList()) {
            if (matches(request, key)) {
                return true;
            }
        }
        return false;
    }


    /**
     * Removes the first request matching a normalized key, keeping every
     * other request in its original order. Rotates through the queue
     * exactly once. The caller must check contains() first.
     */
    private void removeFrom(
        LinkedQueue<HelpRequest> queue,
        String key) {
        int originalSize = queue.size();
        boolean removed = false;
        for (int i = 0; i < originalSize; i++) {
            HelpRequest request = queue.dequeue();
            if (!removed && matches(request, key)) {
                removed = true;
            }
            else {
                queue.enqueue(request);
            }
        }
    }


    /**
     * Checks whether a request's identifier matches a normalized key.
     */
    private boolean matches(HelpRequest request, String key) {
        return request.getStudentId().equals(key);
    }
}
