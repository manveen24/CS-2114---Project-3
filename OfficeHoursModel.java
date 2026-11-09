import java.util.List;
import java.util.NoSuchElementException;
 
/**
 * Model for an office hours waiting line. Owns the FIFO queue of help
 * requests and enforces session rules: requests are validated, student
 * identifiers are unique among active (waiting) requests, and served or
 * canceled students may join again.
 *
 * The model never reads input or prints output. It returns values or throws
 * exceptions, and the controller decides how to present them.
 *
 * @author Luke
 * @version 2026.11.08
 */
public class OfficeHoursModel {
 
    private final LinkedQueue<HelpRequest> waiting;
 
    /**
     * Creates a model with an empty waiting line.
     */
    public OfficeHoursModel() {
        waiting = new LinkedQueue<>();
    }
 
 
    /**
     * Adds a student to the rear of the waiting line.
     *
     * @param name
     *            the student's name
     * @param studentId
     *            the student's identifier
     * @param topic
     *            the help topic
     * @return the student's one-based position in line
     * @throws IllegalArgumentException
     *             if any field is invalid; the line is left unchanged
     * @throws IllegalStateException
     *             if a request with the same normalized identifier is
     *             already waiting; the line is left unchanged
     */
    public int join(String name, String studentId, String topic) {
        // HelpRequest's constructor validates every field before we touch
        // the queue, so an invalid join cannot change state.
        HelpRequest request = new HelpRequest(name, studentId, topic);
        if (isWaiting(request.getStudentId())) {
            throw new IllegalStateException(
                "A request with that identifier is already waiting.");
        }
        waiting.enqueue(request);
        return waiting.size();
    }
 
 
    /**
     * Removes and returns the student at the front of the waiting line.
     *
     * @return the request that was served
     * @throws NoSuchElementException
     *             if no students are waiting
     */
    public HelpRequest callNext() {
        return waiting.dequeue();
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
 
        // Search a snapshot first so an absent identifier never mutates
        // the queue.
        if (!containsNormalized(key)) {
            return false;
        }
 
        // Rotate through the queue exactly once, dropping the match and
        // re-enqueueing every other request in its original order.
        int originalSize = waiting.size();
        boolean removed = false;
        for (int i = 0; i < originalSize; i++) {
            HelpRequest request = waiting.dequeue();
            if (!removed && matches(request, key)) {
                removed = true;
            }
            else {
                waiting.enqueue(request);
            }
        }
        return removed;
    }
 
 
    /**
     * Checks whether a request with the given identifier is waiting.
     *
     * @param studentId
     *            the identifier to look for (normalized before comparing)
     * @return true if a matching request is waiting
     * @throws IllegalArgumentException
     *             if the identifier is invalid
     */
    public boolean isWaiting(String studentId) {
        return containsNormalized(normalize(studentId));
    }
 
 
    /**
     * Returns a snapshot of the waiting line in FIFO order. Changing the
     * returned list does not change the model. A shallow copy is enough
     * because HelpRequest objects are immutable.
     *
     * @return the waiting requests, front of the line first
     */
    public List<HelpRequest> getWaitingRequests() {
        return waiting.toList();
    }
 
 
    /**
     * Returns how many students are waiting.
     *
     * @return the number of waiting requests
     */
    public int getWaitingCount() {
        return waiting.size();
    }
 
 
    /**
     * Checks whether the waiting line is empty.
     *
     * @return true if no students are waiting
     */
    public boolean isEmpty() {
        return waiting.isEmpty();
    }
 
    /**
     * Validates and normalizes an identifier using HelpRequest's shared
     * rule, so joins and cancellations compare identifiers the same way.
     */
    private String normalize(String studentId) {
        return HelpRequest.normalizeStudentId(studentId);
    }
 
 
    /**
     * Checks whether a waiting request matches an already normalized key.
     */
    private boolean containsNormalized(String key) {
        for (HelpRequest request : waiting.toList()) {
            if (matches(request, key)) {
                return true;
            }
        }
        return false;
    }
 
 
    /**
     * Checks whether a request's identifier matches a normalized key.
     */
    private boolean matches(HelpRequest request, String key) {
        return normalize(request.getStudentId()).equals(key);
    }
}
 
