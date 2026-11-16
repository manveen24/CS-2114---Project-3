import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

/**
 * Tests the HelpCategory priority rules and menu-choice parsing.
 *
 * @author Luke
 * @version 2026.11.16
 */
public class HelpCategoryTest
{
    /**
     * Tests which categories are priority.
     */
    @Test
    public void testPriorityLevels()
    {
        assertTrue(HelpCategory.DUE_TODAY.isPriority());
        assertTrue(HelpCategory.QUICK_QUESTION.isPriority());
        assertFalse(HelpCategory.DEBUGGING.isPriority());
        assertFalse(HelpCategory.CONCEPT.isPriority());
    }


    /**
     * Tests that every category has a label.
     */
    @Test
    public void testLabels()
    {
        for (HelpCategory category : HelpCategory.values())
        {
            assertNotNull(category.getLabel());
            assertFalse(category.getLabel().isEmpty());
        }
    }


    /**
     * Tests converting valid menu choices.
     */
    @Test
    public void testFromMenuChoice()
    {
        assertEquals(HelpCategory.DUE_TODAY, HelpCategory.fromMenuChoice("1"));
        assertEquals(HelpCategory.QUICK_QUESTION,
            HelpCategory.fromMenuChoice(" 2 "));
        assertEquals(HelpCategory.DEBUGGING, HelpCategory.fromMenuChoice("3"));
        assertEquals(HelpCategory.CONCEPT, HelpCategory.fromMenuChoice("4"));
    }


    /**
     * Tests rejecting invalid menu choices.
     */
    @Test
    public void testInvalidMenuChoice()
    {
        String[] invalid = { null, "", " ", "0", "5", "-1", "abc", "1.5" };

        for (String choice : invalid)
        {
            assertThrows(IllegalArgumentException.class,
                () -> HelpCategory.fromMenuChoice(choice));
        }
    }
}
