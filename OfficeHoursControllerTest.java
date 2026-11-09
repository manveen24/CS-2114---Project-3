
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Scanner;

import student.TestCase;

/**
 * Tests the OfficeHoursController class.
 *
 * @author Jacob Dial
 * @version 2026.11.09
 */
public class OfficeHoursControllerTest
    extends TestCase
{
    private OfficeHoursModel model;
    private OfficeHoursView view;
    private OfficeHoursController controller;
    private ByteArrayOutputStream output;

    /**
     * Sets up the model, view, and controller before each test.
     */
    public void setUp()
    {
        model = new OfficeHoursModel();
        output = new ByteArrayOutputStream();

        view = new OfficeHoursView(new Scanner(""), new PrintStream(output));

        controller = new OfficeHoursController(model, view);
    }


    /**
     * Creates a controller with specific simulated user input.
     *
     * @param input
     *            the simulated console input
     */
    private void createController(String input)
    {
        model = new OfficeHoursModel();
        output = new ByteArrayOutputStream();

        view = new OfficeHoursView(new Scanner(input), new PrintStream(output));

        controller = new OfficeHoursController(model, view);
    }


    /**
     * Tests that the constructor creates a controller successfully.
     */
    public void testConstructor()
    {
        assertNotNull(controller);
    }


    /**
     * Tests that a null model is rejected.
     */
    public void testNullModel()
    {
        try
        {
            new OfficeHoursController(null, view);
            fail("Expected IllegalArgumentException");
        }
        catch (IllegalArgumentException e)
        {
            assertNotNull(e.getMessage());
        }
    }


    /**
     * Tests that a null view is rejected.
     */
    public void testNullView()
    {
        try
        {
            new OfficeHoursController(model, null);
            fail("Expected IllegalArgumentException");
        }
        catch (IllegalArgumentException e)
        {
            assertNotNull(e.getMessage());
        }
    }


    /**
     * Tests that a student can successfully join the waiting line.
     */
    public void testJoinRequest()
    {
        createController("Jacob\n001\nJava\n");

        controller.joinRequest();

        assertEquals(1, model.getWaitingCount());
        assertEquals("001", model.getWaitingRequests().get(0).getStudentId());

        assertTrue(output.toString().contains("Successfully joined"));
    }


    /**
     * Tests that invalid student information is rejected.
     */
    public void testJoinRequestInvalidInput()
    {
        createController(" \n001\nJava\n");

        controller.joinRequest();

        assertEquals(0, model.getWaitingCount());

        assertTrue(output.toString().contains("Unable to join"));
    }


    /**
     * Tests that duplicate student IDs are rejected.
     */
    public void testJoinRequestDuplicate()
    {
        model.join("Jacob", "001", "Java");

        createController("Alex\n001\nQueues\n");

        controller.joinRequest();

        assertEquals(1, model.getWaitingCount());

        assertTrue(output.toString().contains("Unable to join"));
    }


    /**
     * Tests that EOF during joining exits without adding a request.
     */
    public void testJoinRequestEOF()
    {
        createController("Jacob\n");

        controller.joinRequest();

        assertEquals(0, model.getWaitingCount());
    }


    /**
     * Tests displaying the waiting list.
     */
    public void testShowWaitingList()
    {
        model.join("Jacob", "001", "Java");
        model.join("Alex", "002", "Queues");

        controller.showWaitingList();

        assertTrue(output.toString().contains("Jacob"));
        assertTrue(output.toString().contains("Alex"));
    }


    /**
     * Tests displaying an empty waiting list.
     */
    public void testShowEmptyWaitingList()
    {
        controller.showWaitingList();

        assertEquals(0, model.getWaitingCount());
    }


    /**
     * Tests calling the next student in the waiting line.
     */
    public void testCallNextRequest()
    {
        model.join("Jacob", "001", "Java");
        model.join("Alex", "002", "Queues");

        controller.callNextRequest();

        assertEquals(1, model.getWaitingCount());

        assertEquals("002", model.getWaitingRequests().get(0).getStudentId());

        assertTrue(output.toString().contains("Jacob"));
    }


    /**
     * Tests calling the next student when nobody is waiting.
     */
    public void testCallNextRequestEmpty()
    {
        controller.callNextRequest();

        assertEquals(0, model.getWaitingCount());

        assertTrue(output.toString().contains("No students waiting."));
    }


    /**
     * Tests successfully canceling a student request.
     */
    public void testCancelRequest()
    {
        model.join("Jacob", "001", "Java");
        model.join("Alex", "002", "Queues");

        createController("001\n");

        model.join("Jacob", "001", "Java");
        model.join("Alex", "002", "Queues");

        controller.cancelRequest();

        assertEquals(1, model.getWaitingCount());

        assertEquals("002", model.getWaitingRequests().get(0).getStudentId());

        assertTrue(output.toString().contains("successfully canceled"));
    }


    /**
     * Tests canceling a student who is not waiting.
     */
    public void testCancelRequestNotFound()
    {
        createController("999\n");

        controller.cancelRequest();

        assertEquals(0, model.getWaitingCount());

        assertTrue(output.toString().contains("No matching request."));
    }


    /**
     * Tests that an invalid cancellation ID is rejected.
     */
    public void testCancelRequestInvalidID()
    {
        model.join("Jacob", "001", "Java");

        createController(" \n");

        model.join("Jacob", "001", "Java");

        controller.cancelRequest();

        assertEquals(1, model.getWaitingCount());

        assertTrue(output.toString().contains("Unable to cancel"));
    }


    /**
     * Tests that EOF during cancellation exits without changing the queue.
     */
    public void testCancelRequestEOF()
    {
        model.join("Jacob", "001", "Java");

        createController("");

        model.join("Jacob", "001", "Java");

        controller.cancelRequest();

        assertEquals(1, model.getWaitingCount());
    }


    /**
     * Tests that an invalid menu choice displays an error.
     */
    public void testInvalidMenuChoice()
    {
        createController("9\n5\n");

        controller.run();

        assertTrue(output.toString().contains("Invalid choice"));
    }


    /**
     * Tests that choosing option 5 exits the application.
     */
    public void testExit()
    {
        createController("5\n");

        controller.run();

        assertTrue(output.toString().contains("Goodbye"));
    }


    /**
     * Tests that EOF at the menu exits without throwing an exception.
     */
    public void testRunEOF()
    {
        createController("");

        controller.run();

        assertEquals(0, model.getWaitingCount());
    }
}
