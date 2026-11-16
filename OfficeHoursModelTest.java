import java.util.List;
import java.util.NoSuchElementException;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
 
/**
 * Tests the office hours rules in OfficeHoursModel without a view or
 * controller.
 *
 * @author Luke
 * @version 2026.11.09
 */
public class OfficeHoursModelTest
{
    private OfficeHoursModel model;
 
    /**
     * Creates an empty model before each test.
     */
    @BeforeEach
    public void setUp()
    {
        model = new OfficeHoursModel();
    }
 
 
    /**
     * Tests that a new model starts empty.
     */
    @Test
    public void testNewModel()
    {
        assertTrue(model.isEmpty());
        assertEquals(0, model.getWaitingCount());
        assertTrue(model.getWaitingRequests().isEmpty());
    }
 
 
    /**
     * Tests that join returns one-based positions.
     */
    @Test
    public void testJoinPositions()
    {
        assertEquals(1, model.join("Ana", "Ab01", "Loops"));
        assertEquals(2, model.join("Ben", "B02", "Queues"));
        assertEquals(3, model.join("Cal", "C03", "Recursion"));
        assertEquals(3, model.getWaitingCount());
        assertFalse(model.isEmpty());
        assertEquals("ab01,b02,c03", ids());
    }
 
 
    /**
     * Tests that callNext serves students in FIFO order.
     */
    @Test
    public void testCallNextFifo()
    {
        model.join("Ana", "Ab01", "Loops");
        model.join("Ben", "B02", "Queues");
 
        assertEquals("Ana", model.callNext().getName());
        assertEquals(1, model.getWaitingCount());
        assertEquals("Ben", model.callNext().getName());
        assertEquals(0, model.getWaitingCount());
        assertTrue(model.isEmpty());
    }
 
 
    /**
     * Tests that callNext on an empty line throws.
     */
    @Test
    public void testCallNextEmpty()
    {
        try
        {
            model.callNext();
            fail("Expected NoSuchElementException.");
        }
        catch (NoSuchElementException exception)
        {
            assertNotNull(exception.getMessage());
        }
 
        assertEquals(0, model.getWaitingCount());
    }
 
 
    /**
     * Tests that duplicates are rejected after trimming and case
     * normalization, without changing the line.
     */
    @Test
    public void testDuplicateRejected()
    {
        model.join("Ana", "Ab01", "Loops");
        model.join("Ben", "B02", "Queues");
 
        String[] duplicates = { "Ab01", " ab01 ", "AB01", "  aB01" };
 
        for (String duplicate : duplicates)
        {
            try
            {
                model.join("Other", duplicate, "Topic");
                fail("Expected IllegalStateException.");
            }
            catch (IllegalStateException exception)
            {
                assertNotNull(exception.getMessage());
            }
 
            assertEquals(2, model.getWaitingCount());
            assertEquals("ab01,b02", ids());
        }
    }
 
 
    /**
     * Tests that invalid joins leave the line unchanged.
     */
    @Test
    public void testInvalidJoinUnchanged()
    {
        model.join("Ana", "Ab01", "Loops");
 
        assertInvalidJoin(null, "X1", "Topic");
        assertInvalidJoin("Ben", null, "Topic");
        assertInvalidJoin("Ben", "X1", null);
        assertInvalidJoin(" ", "X1", "Topic");
        assertInvalidJoin("Ben", "   ", "Topic");
        assertInvalidJoin("Ben", "X1", "");
        assertInvalidJoin("Ben", "X\t1", "Topic");
        assertInvalidJoin("Ben", "1234567890123456789012345678901", "Topic");
    }
 
 
    /**
     * Tests canceling the front request.
     */
    @Test
    public void testCancelFront()
    {
        joinThree();
 
        assertTrue(model.cancel("A1"));
        assertEquals(2, model.getWaitingCount());
        assertEquals("b2,c3", ids());
    }
 
 
    /**
     * Tests canceling a middle request.
     */
    @Test
    public void testCancelMiddle()
    {
        joinThree();
 
        assertTrue(model.cancel(" b2 "));
        assertEquals(2, model.getWaitingCount());
        assertEquals("a1,c3", ids());
    }
 
 
    /**
     * Tests canceling the rear request.
     */
    @Test
    public void testCancelRear()
    {
        joinThree();
 
        assertTrue(model.cancel("C3"));
        assertEquals(2, model.getWaitingCount());
        assertEquals("a1,b2", ids());
    }
 
 
    /**
     * Tests canceling the only request.
     */
    @Test
    public void testCancelOnly()
    {
        model.join("Ana", "Ab01", "Loops");
 
        assertTrue(model.cancel("ab01"));
        assertTrue(model.isEmpty());
        assertEquals(0, model.getWaitingCount());
        assertEquals("", ids());
    }
 
 
    /**
     * Tests that canceling an absent valid identifier returns false.
     */
    @Test
    public void testCancelAbsent()
    {
        joinThree();
 
        assertFalse(model.cancel("zz99"));
        assertEquals(3, model.getWaitingCount());
        assertEquals("a1,b2,c3", ids());
 
        OfficeHoursModel empty = new OfficeHoursModel();
        assertFalse(empty.cancel("a1"));
        assertEquals(0, empty.getWaitingCount());
    }
 
 
    /**
     * Tests that invalid cancellation identifiers leave the line unchanged.
     */
    @Test
    public void testInvalidCancelUnchanged()
    {
        joinThree();
 
        assertInvalidCancel(null);
        assertInvalidCancel("");
        assertInvalidCancel("   ");
        assertInvalidCancel("a\n1");
        assertInvalidCancel("1234567890123456789012345678901");
    }
 
 
    /**
     * Tests that a served student can join again.
     */
    @Test
    public void testRejoinAfterServe()
    {
        model.join("Ana", "Ab01", "Loops");
        model.join("Ben", "B02", "Queues");
        model.callNext();
 
        assertFalse(model.isWaiting("ab01"));
        assertEquals(2, model.join("Ana", "AB01", "Loops"));
        assertEquals("b02,ab01", ids());
    }
 
 
    /**
     * Tests that a canceled student can join again.
     */
    @Test
    public void testRejoinAfterCancel()
    {
        model.join("Ana", "Ab01", "Loops");
        model.cancel("ab01");
 
        assertEquals(1, model.join("Ana", " AB01 ", "Loops"));
        assertEquals("ab01", ids());
    }
 
 
    /**
     * Tests isWaiting with normalized identifiers.
     */
    @Test
    public void testIsWaiting()
    {
        model.join("Ana", "Ab01", "Loops");
 
        assertTrue(model.isWaiting("ab01"));
        assertTrue(model.isWaiting(" AB01 "));
        assertFalse(model.isWaiting("b02"));
    }
 
 
    /**
     * Tests that a returned snapshot does not expose the queue.
     */
    @Test
    public void testSnapshotIndependence()
    {
        model.join("Ana", "Ab01", "Loops");
        model.join("Ben", "B02", "Queues");
 
        List<HelpRequest> snapshot = model.getWaitingRequests();
        snapshot.clear();
 
        assertEquals(2, model.getWaitingCount());
        assertEquals("ab01,b02", ids());
    }
 
 
    /**
     * Tests that the count stays correct through a mix of operations.
     */
    @Test
    public void testCountsThroughOperations()
    {
        model.join("Ana", "Ab01", "Loops");
        assertEquals(1, model.getWaitingCount());
 
        model.join("Ben", "B02", "Queues");
        assertEquals(2, model.getWaitingCount());
 
        model.callNext();
        assertEquals(1, model.getWaitingCount());
 
        model.cancel("absent");
        assertEquals(1, model.getWaitingCount());
 
        model.cancel("b02");
        assertEquals(0, model.getWaitingCount());
 
        model.join("Ana", "AB01", "Loops");
        assertEquals(1, model.getWaitingCount());
    }
 
 
    /**
     * Runs the README demonstration sequence against the model.
     */
    @Test
    public void testReadmeDemonstration()
    {
        assertEquals(1, model.join("Ana", "Ab01", "Loops"));
        assertEquals(2, model.join("Ben", "B02", "Queues"));
        assertEquals("ab01,b02", ids());
 
        try
        {
            model.join("Other", " ab01 ", "Topic");
            fail("Expected IllegalStateException.");
        }
        catch (IllegalStateException exception)
        {
            assertNotNull(exception.getMessage());
        }
 
        assertEquals("Ana", model.callNext().getName());
        assertEquals("b02", ids());
        assertTrue(model.cancel("b02"));
        assertTrue(model.isEmpty());
 
        try
        {
            model.callNext();
            fail("Expected NoSuchElementException.");
        }
        catch (NoSuchElementException exception)
        {
            assertNotNull(exception.getMessage());
        }
 
        assertEquals(1, model.join("Ana", "AB01", "Loops"));
    }
 
 
    // ----------------------------------------------------------------------
    // Helpers
    // ----------------------------------------------------------------------
 
    /**
     * Adds three requests with identifiers a1, b2, and c3.
     */
    private void joinThree()
    {
        model.join("Ana", "A1", "Loops");
        model.join("Ben", "B2", "Queues");
        model.join("Cal", "C3", "Recursion");
    }
 
 
    /**
     * Returns the waiting identifiers in order, separated by commas.
     *
     * @return the ordered identifiers
     */
    private String ids()
    {
        StringBuilder result = new StringBuilder();
 
        for (HelpRequest request : model.getWaitingRequests())
        {
            if (result.length() > 0)
            {
                result.append(",");
            }
            result.append(request.getStudentId());
        }
 
        return result.toString();
    }
 
 
    /**
     * Verifies that an invalid join throws and changes nothing.
     *
     * @param name
     *            the name to try
     * @param studentId
     *            the identifier to try
     * @param topic
     *            the topic to try
     */
    private void assertInvalidJoin(String name, String studentId, String topic)
    {
        int count = model.getWaitingCount();
        String before = ids();
 
        try
        {
            model.join(name, studentId, topic);
            fail("Expected IllegalArgumentException.");
        }
        catch (IllegalArgumentException exception)
        {
            assertNotNull(exception.getMessage());
        }
 
        assertEquals(count, model.getWaitingCount());
        assertEquals(before, ids());
    }
 
 
    /**
     * Verifies that an invalid cancellation throws and changes nothing.
     *
     * @param studentId
     *            the identifier to try
     */
    private void assertInvalidCancel(String studentId)
    {
        int count = model.getWaitingCount();
        String before = ids();
 
        try
        {
            model.cancel(studentId);
            fail("Expected IllegalArgumentException.");
        }
        catch (IllegalArgumentException exception)
        {
            assertNotNull(exception.getMessage());
        }
 
        assertEquals(count, model.getWaitingCount());
        assertEquals(before, ids());
    }

    // ----------------------------------------------------------------------
    // Priority by category
    // ----------------------------------------------------------------------

    /**
     * Tests that the three-argument join uses a regular category.
     */
    @Test
    public void testDefaultJoinIsRegular()
    {
        model.join("Ana", "A1", "Loops");

        assertEquals(0, model.getPriorityCount());
        assertFalse(model.getWaitingRequests().get(0).isUrgent());
    }


    /**
     * Tests that priority categories go ahead of regular requests.
     */
    @Test
    public void testPriorityCategoryServedFirst()
    {
        assertEquals(1, model.join("Ana", "A1", "Loops",
            HelpCategory.CONCEPT));
        assertEquals(2, model.join("Ben", "B2", "Queues",
            HelpCategory.DEBUGGING));
        assertEquals(1, model.join("Cal", "C3", "Exam",
            HelpCategory.DUE_TODAY));

        assertEquals("c3,a1,b2", ids());
        assertEquals(1, model.getPriorityCount());
        assertTrue(model.getWaitingRequests().get(0).isUrgent());
        assertEquals("Cal", model.callNext().getName());
        assertEquals("Ana", model.callNext().getName());
        assertEquals("Ben", model.callNext().getName());
    }


    /**
     * Tests that both priority categories share one FIFO level.
     */
    @Test
    public void testFifoWithinPriorityLevel()
    {
        model.join("Ana", "A1", "Loops", HelpCategory.CONCEPT);
        model.join("Cal", "C3", "Exam", HelpCategory.DUE_TODAY);
        model.join("Ben", "B2", "Queues", HelpCategory.DEBUGGING);
        model.join("Dee", "D4", "Syntax", HelpCategory.QUICK_QUESTION);

        assertEquals("c3,d4,a1,b2", ids());
        assertEquals(5, model.join("Eve", "E5", "Lists",
            HelpCategory.CONCEPT));
        assertEquals(3, model.join("Fay", "F6", "Due",
            HelpCategory.QUICK_QUESTION));
        assertEquals("c3,d4,f6,a1,b2,e5", ids());
    }


    /**
     * Tests that a null category is rejected without a change.
     */
    @Test
    public void testNullCategoryRejected()
    {
        model.join("Ana", "A1", "Loops");

        assertThrows(IllegalArgumentException.class,
            () -> model.join("Ben", "B2", "Queues", null));

        assertEquals("a1", ids());
        assertEquals(1, model.getWaitingCount());
    }


    /**
     * Tests that duplicates are rejected across both priority levels.
     */
    @Test
    public void testDuplicateAcrossLevels()
    {
        model.join("Cal", "C3", "Exam", HelpCategory.DUE_TODAY);
        model.join("Ana", "A1", "Loops");

        assertThrows(IllegalStateException.class,
            () -> model.join("Other", "c3", "Topic"));
        assertThrows(IllegalStateException.class,
            () -> model.join("Other", " A1 ", "Topic",
                HelpCategory.QUICK_QUESTION));

        assertEquals("c3,a1", ids());
    }


    /**
     * Tests canceling a priority request.
     */
    @Test
    public void testCancelPriority()
    {
        model.join("Cal", "C3", "Exam", HelpCategory.DUE_TODAY);
        model.join("Dee", "D4", "Syntax", HelpCategory.QUICK_QUESTION);
        model.join("Ana", "A1", "Loops");

        assertTrue(model.cancel("c3"));
        assertEquals("d4,a1", ids());
        assertEquals(1, model.getPriorityCount());
        assertFalse(model.isWaiting("c3"));
    }


    /**
     * Tests that callNext falls back to regular requests once the priority
     * level is empty.
     */
    @Test
    public void testCallNextAfterPriorityEmpties()
    {
        model.join("Ana", "A1", "Loops");
        model.join("Cal", "C3", "Exam", HelpCategory.DUE_TODAY);

        assertEquals("Cal", model.callNext().getName());
        assertEquals(0, model.getPriorityCount());
        assertEquals("Ana", model.callNext().getName());
        assertTrue(model.isEmpty());
        assertThrows(NoSuchElementException.class, () -> model.callNext());
    }


    /**
     * Tests that a served priority student can rejoin in a regular
     * category.
     */
    @Test
    public void testRejoinInDifferentCategory()
    {
        model.join("Cal", "C3", "Exam", HelpCategory.DUE_TODAY);
        model.callNext();

        assertEquals(1, model.join("Cal", "C3", "Exam",
            HelpCategory.CONCEPT));
        assertFalse(model.getWaitingRequests().get(0).isUrgent());
    }


    /**
     * Tests that wait estimates follow priority service order.
     */
    @Test
    public void testEstimatesFollowPriorityOrder()
    {
        model.join("Ana", "A1", "Loops");
        model.join("Ben", "B2", "Queues", HelpCategory.QUICK_QUESTION);

        assertEquals("b2,a1", ids());
        assertEquals("[0, 30]", model.getEstimatedWaitTimes().toString());
        assertEquals(30, model.getEstimatedWaitMinutes(2));
    }


    /**
     * Tests a long interleaving of joins, calls, and cancellations across
     * both levels.
     */
    @Test
    public void testInterleavedPriorityOperations()
    {
        model.join("Ana", "A1", "Loops");
        model.join("Ben", "B2", "Queues", HelpCategory.QUICK_QUESTION);
        model.join("Cal", "C3", "Exam", HelpCategory.DUE_TODAY);
        model.join("Dee", "D4", "Bug", HelpCategory.DEBUGGING);

        assertEquals("b2,c3,a1,d4", ids());
        assertEquals("Ben", model.callNext().getName());
        assertTrue(model.cancel("a1"));
        model.join("Eve", "E5", "Lists", HelpCategory.DUE_TODAY);

        assertEquals("c3,e5,d4", ids());
        assertEquals(2, model.getPriorityCount());
        assertEquals(3, model.getWaitingCount());
    }
}
