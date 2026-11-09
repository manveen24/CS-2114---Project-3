import java.util.Locale;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
 
/**
 * Tests validation and stored values in HelpRequest.
 *
 * @author Manveen Sidhu
 * @version 2026.11.07
 */
public class HelpRequestTest
{
    private HelpRequest request;
 
    /**
     * Creates a request before each test.
     */
    @Before
    public void setUp()
    {
        request = new HelpRequest(" Ana ", " Ab01 ", " Loops ");
    }
 
 
    /**
     * Tests trimming and getter results.
     */
    @Test
    public void testGetters()
    {
        assertEquals("Ana", request.getName());
        assertEquals("ab01", request.getStudentId());
        assertEquals("Loops", request.getTopic());
    }
 
 
    /**
     * Tests that internal spaces and case are preserved.
     */
    @Test
    public void testInternalSpaces()
    {
        HelpRequest spaced =
            new HelpRequest(" Ana Maria ", " AB 01 ", " Nested Loops ");
 
        assertEquals("Ana Maria", spaced.getName());
        assertEquals("ab 01", spaced.getStudentId());
        assertEquals("Nested Loops", spaced.getTopic());
    }
 
 
    /**
     * Tests that identifiers keep leading zeroes.
     */
    @Test
    public void testLeadingZeroes()
    {
        HelpRequest numbered = new HelpRequest("Ana", "001", "Loops");
 
        assertEquals("001", numbered.getStudentId());
    }
 
 
    /**
     * Tests valid minimum field lengths.
     */
    @Test
    public void testMinimumLengths()
    {
        HelpRequest minimum = new HelpRequest("A", "B", "C");
 
        assertEquals("A", minimum.getName());
        assertEquals("b", minimum.getStudentId());
        assertEquals("C", minimum.getTopic());
    }
 
 
    /**
     * Tests exact maximum lengths after trimming.
     */
    @Test
    public void testMaximumLengths()
    {
        String name = repeat('A', 60);
        String id = repeat('B', 30);
        String topic = repeat('C', 200);
 
        HelpRequest maximum = new HelpRequest(
            " " + name + " ",
            " " + id + " ",
            " " + topic + " ");
 
        assertEquals(name, maximum.getName());
        assertEquals(id.toLowerCase(Locale.ROOT), maximum.getStudentId());
        assertEquals(topic, maximum.getTopic());
    }
 
 
    /**
     * Tests values one character over each limit.
     */
    @Test
    public void testOverMaximumLengths()
    {
        assertInvalid(repeat('A', 61), "ab01", "Loops");
        assertInvalid("Ana", repeat('B', 31), "Loops");
        assertInvalid("Ana", "ab01", repeat('C', 201));
    }
 
 
    /**
     * Tests null values in every field.
     */
    @Test
    public void testNullFields()
    {
        assertInvalid(null, "ab01", "Loops");
        assertInvalid("Ana", null, "Loops");
        assertInvalid("Ana", "ab01", null);
    }
 
 
    /**
     * Tests empty and space-only values in every field.
     */
    @Test
    public void testBlankFields()
    {
        String[] blanks = { "", " ", "   " };
 
        for (String blank : blanks)
        {
            assertInvalid(blank, "ab01", "Loops");
            assertInvalid("Ana", blank, "Loops");
            assertInvalid("Ana", "ab01", blank);
        }
    }
 
 
    /**
     * Tests all ISO control characters in every field.
     */
    @Test
    public void testControlCharacters()
    {
        for (int code = 0; code <= 159; code++)
        {
            char control = (char)code;
 
            if (Character.isISOControl(control))
            {
                String invalid = "A" + control + "B";
 
                assertInvalid(invalid, "ab01", "Loops");
                assertInvalid("Ana", invalid, "Loops");
                assertInvalid("Ana", "ab01", invalid);
            }
        }
    }
 
 
    /**
     * Tests that controls are rejected before trimming.
     */
    @Test
    public void testControlsAtEdges()
    {
        assertInvalid("\tAna", "ab01", "Loops");
        assertInvalid("Ana", "ab01\n", "Loops");
        assertInvalid("Ana", "ab01", "\rLoops");
    }
 
 
    /**
     * Tests printable Unicode.
     */
    @Test
    public void testUnicode()
    {
        HelpRequest unicode = new HelpRequest("Zoë", "É01", "Résumé");
 
        assertEquals("Zoë", unicode.getName());
        assertEquals("é01", unicode.getStudentId());
        assertEquals("Résumé", unicode.getTopic());
    }
 
 
    /**
     * Tests normalization independently of the default locale.
     */
    @Test
    public void testLocaleIndependentNormalization()
    {
        Locale original = Locale.getDefault();
 
        try
        {
            Locale.setDefault(new Locale("tr", "TR"));
 
            HelpRequest normalized = new HelpRequest("Ana", "ID01", "Loops");
 
            assertEquals("id01", normalized.getStudentId());
        }
        finally
        {
            Locale.setDefault(original);
        }
    }
 
 
    /**
     * Tests that ordinary requests are not urgent.
     */
    @Test
    public void testNormalRequest()
    {
        assertFalse(request.isUrgent());
 
        HelpRequest normal = new HelpRequest("Ana", "ab01", "Loops", false);
 
        assertFalse(normal.isUrgent());
    }
 
 
    /**
     * Tests approved urgency.
     */
    @Test
    public void testApprovedUrgentRequest()
    {
        HelpRequest urgent =
            new HelpRequest(" Ana ", " AB01 ", " Loops ", true);
 
        assertTrue(urgent.isUrgent());
        assertEquals("Ana", urgent.getName());
        assertEquals("ab01", urgent.getStudentId());
        assertEquals("Loops", urgent.getTopic());
    }
 
 
    /**
     * Tests that the urgent constructor also validates input.
     */
    @Test
    public void testUrgentConstructorValidation()
    {
        try
        {
            new HelpRequest("Ana", " ", "Loops", true);
            fail("Expected IllegalArgumentException.");
        }
        catch (IllegalArgumentException exception)
        {
            assertNotNull(exception.getMessage());
            assertFalse(exception.getMessage().isEmpty());
        }
    }
 
 
    /**
     * Verifies rejection of invalid constructor arguments.
     *
     * @param name
     *            the name to test
     * @param studentId
     *            the identifier to test
     * @param topic
     *            the topic to test
     */
    private void assertInvalid(String name, String studentId, String topic)
    {
        try
        {
            new HelpRequest(name, studentId, topic);
            fail("Expected IllegalArgumentException.");
        }
        catch (IllegalArgumentException exception)
        {
            assertNotNull(exception.getMessage());
            assertFalse(exception.getMessage().isEmpty());
        }
    }
 
 
    /**
     * Builds a string of the requested length.
     *
     * @param value
     *            the character to repeat
     * @param count
     *            the number of repetitions
     * @return the resulting string
     */
    private String repeat(char value, int count)
    {
        StringBuilder result = new StringBuilder();
 
        for (int i = 0; i < count; i++)
        {
            result.append(value);
        }
 
        return result.toString();
    }
}
 