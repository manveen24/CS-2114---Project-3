
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests for OfficeHoursView.
 *
 * @author Shrey Agarwal
 * @version 2026-11-16
 */
public class OfficeHoursViewTest {

    private OfficeHoursView view;
    private ByteArrayOutputStream output;

    @BeforeEach
    public void setUp() {
        output = new ByteArrayOutputStream();
        view = new OfficeHoursView(
            new Scanner("1\nShrey\n"),
            new PrintStream(output));
    }

    @Test
    public void testDisplayMenu() {
        view.displayMenu();

        String result = output.toString();

        assertTrue(result.contains("Join waiting line"));
        assertTrue(result.contains("View waiting line"));
        assertTrue(result.contains("Call next student"));
        assertTrue(result.contains("Cancel request"));
        assertTrue(result.contains("Exit"));
    }

    @Test
    public void testReadLine() {
        assertEquals("1", view.readLine("Choice: "));
        assertEquals("Shrey", view.readLine("Name: "));
        assertTrue(output.toString().contains("Choice: "));
    }

    @Test
    public void testReadLineEOF() {
        OfficeHoursView emptyView = new OfficeHoursView(
            new Scanner(""), new PrintStream(output));

        assertNull(emptyView.readLine("Choice: "));
    }

    @Test
    public void testDisplayMessage() {
        view.displayMessage("Successfully joined");

        assertTrue(output.toString().contains(
            "Successfully joined"));
    }

    @Test
    public void testDisplayCalledRequest() {
        HelpRequest request = new HelpRequest(
            "Shrey", "123", "Java");

        view.displayCalledRequest(request);

        String result = output.toString();

        assertTrue(result.contains("Shrey"));
        assertTrue(result.contains("123"));
        assertTrue(result.contains("Java"));
    }

    @Test
    public void testDisplayWaitingList() {
        ArrayList<HelpRequest> requests = new ArrayList<>();

        requests.add(new HelpRequest("Shrey", "123", "Java"));
        requests.add(new HelpRequest("Jacob", "456", "Queues"));

        view.displayWaitingList(requests, 2);

        String result = output.toString();

        assertTrue(result.contains("Shrey"));
        assertTrue(result.contains("Jacob"));
        assertTrue(result.contains("Total students: 2"));
    }

    @Test
    public void testEmptyWaitingList() {
        ArrayList<HelpRequest> requests = new ArrayList<>();

        view.displayWaitingList(requests, 0);

        assertTrue(output.toString().contains(
            "No students waiting."));
    }

    @Test
    public void testEstimatedWaitingTimes() {
        ArrayList<HelpRequest> requests = new ArrayList<>();

        requests.add(new HelpRequest("Shrey", "123", "Java"));
        requests.add(new HelpRequest("Jacob", "456", "Queues"));

        view.displayWaitingList(
            requests, 2, Arrays.asList(0, 10));

        String result = output.toString();

        assertTrue(result.contains("Estimated wait: 0 minutes"));
        assertTrue(result.contains("Estimated wait: 10 minutes"));
    }

    @Test
    public void testInvalidWaitingList() {
        assertThrows(IllegalArgumentException.class,
            () -> view.displayWaitingList(null, 0));
    }

    @Test
    public void testInvalidEstimatedWait() {
        ArrayList<HelpRequest> requests = new ArrayList<>();

        requests.add(new HelpRequest("Shrey", "123", "Java"));

        assertThrows(IllegalArgumentException.class,
            () -> view.displayWaitingList(
                requests, 1, Arrays.asList(-5)));
    }

    @Test
    public void testNullConstructorInput() {
        assertThrows(IllegalArgumentException.class,
            () -> new OfficeHoursView(
                null, new PrintStream(output)));
    }

    @Test
    public void testNullMessage() {
        assertThrows(IllegalArgumentException.class,
            () -> view.displayMessage(null));
    }
}
