import java.util.List;
import java.util.NoSuchElementException;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
 
/**
 * Tests the generic FIFO behavior of LinkedQueue.
 *
 * @author Luke
 * @version 2026.11.09
 */
public class LinkedQueueTest
{
    private LinkedQueue<String> queue;
 
    /**
     * Creates an empty queue before each test.
     */
    @BeforeEach
    public void setUp()
    {
        queue = new LinkedQueue<String>();
    }
 
 
    /**
     * Tests that a new queue is empty with size zero.
     */
    @Test
    public void testConstructor()
    {
        assertTrue(queue.isEmpty());
        assertEquals(0, queue.size());
        assertTrue(queue.toList().isEmpty());
    }
 
 
    /**
     * Tests that enqueue adds at the rear and keeps insertion order.
     */
    @Test
    public void testEnqueueOrder()
    {
        queue.enqueue("A");
        queue.enqueue("B");
        queue.enqueue("C");
 
        assertEquals(3, queue.size());
        assertFalse(queue.isEmpty());
        assertEquals("[A, B, C]", queue.toList().toString());
    }
 
 
    /**
     * Tests that enqueue(null) throws without changing the queue.
     */
    @Test
    public void testEnqueueNull()
    {
        queue.enqueue("A");
 
        try
        {
            queue.enqueue(null);
            fail("Expected IllegalArgumentException.");
        }
        catch (IllegalArgumentException exception)
        {
            assertNotNull(exception.getMessage());
        }
 
        assertEquals(1, queue.size());
        assertEquals("[A]", queue.toList().toString());
    }
 
 
    /**
     * Tests that dequeue returns and removes the oldest entry.
     */
    @Test
    public void testDequeue()
    {
        queue.enqueue("A");
        queue.enqueue("B");
 
        assertEquals("A", queue.dequeue());
        assertEquals(1, queue.size());
        assertEquals("B", queue.dequeue());
        assertEquals(0, queue.size());
    }
 
 
    /**
     * Tests that dequeue on an empty queue throws without changing state.
     */
    @Test
    public void testDequeueEmpty()
    {
        try
        {
            queue.dequeue();
            fail("Expected NoSuchElementException.");
        }
        catch (NoSuchElementException exception)
        {
            assertNotNull(exception.getMessage());
        }
 
        assertTrue(queue.isEmpty());
        assertEquals(0, queue.size());
    }
 
 
    /**
     * Tests that peek returns the oldest entry without removing it.
     */
    @Test
    public void testPeek()
    {
        queue.enqueue("A");
        queue.enqueue("B");
 
        assertEquals("A", queue.peek());
        assertEquals("A", queue.peek());
        assertEquals(2, queue.size());
        assertEquals("[A, B]", queue.toList().toString());
    }
 
 
    /**
     * Tests that peek on an empty queue throws.
     */
    @Test
    public void testPeekEmpty()
    {
        try
        {
            queue.peek();
            fail("Expected NoSuchElementException.");
        }
        catch (NoSuchElementException exception)
        {
            assertNotNull(exception.getMessage());
        }
 
        assertEquals(0, queue.size());
    }
 
 
    /**
     * Tests that toList returns a FIFO snapshot without changing the queue.
     */
    @Test
    public void testToListDoesNotModify()
    {
        queue.enqueue("A");
        queue.enqueue("B");
 
        List<String> snapshot = queue.toList();
 
        assertEquals("[A, B]", snapshot.toString());
        assertEquals(2, queue.size());
        assertEquals("A", queue.peek());
    }
 
 
    /**
     * Tests that changing a returned list does not change the queue.
     */
    @Test
    public void testSnapshotIndependence()
    {
        queue.enqueue("A");
        queue.enqueue("B");
 
        List<String> snapshot = queue.toList();
        snapshot.clear();
        snapshot.add("Z");
 
        assertEquals(2, queue.size());
        assertEquals("[A, B]", queue.toList().toString());
    }
 
 
    /**
     * Tests that removing the final entry leaves the queue empty.
     */
    @Test
    public void testFinalRemoval()
    {
        queue.enqueue("A");
        queue.dequeue();
 
        assertTrue(queue.isEmpty());
        assertEquals(0, queue.size());
        assertTrue(queue.toList().isEmpty());
    }
 
 
    /**
     * Tests that the queue can be reused after it becomes empty.
     */
    @Test
    public void testReuseAfterEmpty()
    {
        queue.enqueue("A");
        queue.dequeue();
 
        queue.enqueue("B");
        queue.enqueue("C");
 
        assertEquals(2, queue.size());
        assertEquals("B", queue.peek());
        assertEquals("[B, C]", queue.toList().toString());
        assertEquals("B", queue.dequeue());
        assertEquals("C", queue.dequeue());
        assertTrue(queue.isEmpty());
    }
 
 
    /**
     * Tests the generic queue with HelpRequest objects.
     */
    @Test
    public void testHelpRequestEntries()
    {
        LinkedQueue<HelpRequest> requests = new LinkedQueue<HelpRequest>();
        HelpRequest ana = new HelpRequest("Ana", "Ab01", "Loops");
        HelpRequest ben = new HelpRequest("Ben", "B02", "Queues");
 
        requests.enqueue(ana);
        requests.enqueue(ben);
 
        assertEquals(2, requests.size());
        assertSame(ana, requests.peek());
        assertSame(ana, requests.dequeue());
        assertSame(ben, requests.dequeue());
        assertTrue(requests.isEmpty());
    }
}
 