
import java.io.PrintStream;
import java.util.List;
import java.util.Scanner;

/**
 * Handles the console input and output for OfficeHours.
 *
 * @author Shrey Agarwal
 * @version 2026-11-09
 */
public class OfficeHoursView {

    private final Scanner input;
    private final PrintStream output;

    public OfficeHoursView(Scanner input, PrintStream output) {
        if (input == null || output == null) {
            throw new IllegalArgumentException(
                "Input and output cannot be null");
        }

        this.input = input;
        this.output = output;
    }

    public void displayMenu() {
        output.println();
        output.println("=== OfficeHours ===");
        output.println("1. Join waiting line");
        output.println("2. View waiting line");
        output.println("3. Call next student");
        output.println("4. Cancel request");
        output.println("5. Exit");
    }

    public String readLine(String prompt) {
        if (prompt == null) {
            throw new IllegalArgumentException(
                "Prompt cannot be null");
        }

        output.print(prompt);

        if (!input.hasNextLine()) {
            return null;
        }

        return input.nextLine();
    }

    public void displayMessage(String message) {
        if (message == null) {
            throw new IllegalArgumentException(
                "Message cannot be null");
        }

        output.println(message);
    }

    public void displayCalledRequest(HelpRequest request) {
        if (request == null) {
            throw new IllegalArgumentException(
                "Request cannot be null");
        }

        output.println("Now serving: " + request.getName());
        output.println("Student ID: " + request.getStudentId());
        output.println("Topic: " + request.getTopic());
    }

    public void displayWaitingList(
        List<HelpRequest> requests, int total) {

        if (requests == null || total < 0
            || requests.size() != total) {
            throw new IllegalArgumentException(
                "Invalid waiting list");
        }

        for (HelpRequest request : requests) {
            if (request == null) {
                throw new IllegalArgumentException(
                    "Request cannot be null");
            }
        }

        output.println();
        output.println("=== Waiting Line ===");
        output.println("Total students: " + total);

        if (requests.isEmpty()) {
            output.println("No students waiting.");
            return;
        }

        for (int i = 0; i < requests.size(); i++) {
            HelpRequest request = requests.get(i);

            output.println((i + 1) + ". "
                + request.getName()
                + " (" + request.getStudentId() + ")"
                + " - " + request.getTopic());
        }
    }
}
