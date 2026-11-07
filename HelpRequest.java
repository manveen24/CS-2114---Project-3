import java.util.Locale;

/**
 * Stores one immutable, validated student help request.
 *
 * @author Manveen Sidhu
 * @version 2026.11.07
 */
public final class HelpRequest
{
    private final String name;
    private final String studentId;
    private final String topic;
    private final boolean urgent;

    /**
     * Creates a normal help request.
     *
     * @param name
     *            the student's name
     * @param studentId
     *            the student's identifier
     * @param topic
     *            the topic for which help is needed
     * @throws IllegalArgumentException
     *             if any field is invalid
     */
    public HelpRequest(String name, String studentId, String topic)
    {
        this(name, studentId, topic, false);
    }


    /**
     * Creates a request with the specified approved urgent status. Pending
     * urgency requests should be tracked by the model.
     *
     * @param name
     *            the student's name
     * @param studentId
     *            the student's identifier
     * @param topic
     *            the topic for which help is needed
     * @param urgent
     *            whether urgent status has been approved
     * @throws IllegalArgumentException
     *             if any field is invalid
     */
    public HelpRequest(
        String name,
        String studentId,
        String topic,
        boolean urgent)
    {
        this.name = validateField(name, "Name", 60);
        this.studentId = normalizeStudentId(studentId);
        this.topic = validateField(topic, "Topic", 200);
        this.urgent = urgent;
    }


    /**
     * Returns the trimmed name, preserving its case.
     *
     * @return the student's name
     */
    public String getName()
    {
        return name;
    }


    /**
     * Returns the trimmed, lowercase identifier.
     *
     * @return the normalized identifier
     */
    public String getStudentId()
    {
        return studentId;
    }


    /**
     * Returns the trimmed topic, preserving its case.
     *
     * @return the request topic
     */
    public String getTopic()
    {
        return topic;
    }


    /**
     * Reports whether urgent status has been approved.
     *
     * @return true if approved as urgent
     */
    public boolean isUrgent()
    {
        return urgent;
    }


    /**
     * Validates and normalizes an identifier. Package-private so
     * OfficeHoursModel can reuse it.
     *
     * @param studentId
     *            the raw identifier
     * @return the normalized identifier
     * @throws IllegalArgumentException
     *             if the identifier is invalid
     */
    static String normalizeStudentId(String studentId)
    {
        return validateField(studentId, "Student ID", 30)
            .toLowerCase(Locale.ROOT);
    }


    /**
     * Rejects invalid raw input before trimming.
     *
     * @param value
     *            the raw field
     * @param fieldName
     *            the field's display name
     * @param maxLength
     *            the maximum trimmed length
     * @return the validated, trimmed value
     * @throws IllegalArgumentException
     *             if the field is invalid
     */
    static String validateField(String value, String fieldName, int maxLength)
    {
        if (value == null)
        {
            throw new IllegalArgumentException(fieldName + " cannot be null.");
        }

        for (int i = 0; i < value.length(); i++)
        {
            if (Character.isISOControl(value.charAt(i)))
            {
                throw new IllegalArgumentException(
                    fieldName + " cannot contain control characters.");
            }
        }

        String trimmed = value.trim();

        if (trimmed.isEmpty())
        {
            throw new IllegalArgumentException(fieldName + " cannot be blank.");
        }

        if (trimmed.length() > maxLength)
        {
            throw new IllegalArgumentException(
                fieldName + " must be at most " + maxLength + " characters.");
        }

        return trimmed;
    }
}
