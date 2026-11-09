import java.util.NoSuchElementException;

/**
 * Controls the Office Hours application. Coordinates the model and view and
 * handles user menu choices.
 *
 * @author Jacob Dial
 * @version 2026.11.09
 */
public class OfficeHoursController
{
    private final OfficeHoursModel model;
    private final OfficeHoursView view;
    private boolean running;

    /**
     * Creates an OfficeHoursController.
     *
     * @param model
     *            the office hours model
     * @param view
     *            the office hours view
     * @throws IllegalArgumentException
     *             if either dependency is null
     */
    public OfficeHoursController(OfficeHoursModel model, OfficeHoursView view)
    {
        if (model == null || view == null)
        {
            throw new IllegalArgumentException(
                "Model and view cannot be null.");
        }

        this.model = model;
        this.view = view;
        running = false;
    }


    /**
     * Starts the application and processes menu choices until the user exits or
     * input ends.
     */
    public void run()
    {
        running = true;

        showWaitingList();

        while (running)
        {
            view.displayMenu();

            String choice = view.readLine("Enter your choice: ");

            if (choice == null)
            {
                exit();
                break;
            }

            choice = choice.trim();

            switch (choice)
            {
                case "1":
                    joinRequest();
                    break;

                case "2":
                    showWaitingList();
                    break;

                case "3":
                    callNextRequest();
                    break;

                case "4":
                    cancelRequest();
                    break;

                case "5":
                    exit();
                    view.displayMessage("Exiting Office Hours. Goodbye!");
                    break;

                default:
                    view.displayMessage(
                        "Invalid choice. Please enter a number from 1 to 5.");
                    break;
            }
        }
    }


    /**
     * Collects student information and adds a request to the waiting line.
     */
    public void joinRequest()
    {
        String name = view.readLine("Enter your name: ");

        if (name == null)
        {
            exit();
            return;
        }

        String studentId = view.readLine("Enter your student ID: ");

        if (studentId == null)
        {
            exit();
            return;
        }

        String topic = view.readLine("Enter your help topic: ");

        if (topic == null)
        {
            exit();
            return;
        }

        try
        {
            int position = model.join(name, studentId, topic);

            view.displayMessage(
                "Successfully joined the waiting line. Your position is "
                    + position + ".");

            showWaitingList();
        }
        catch (IllegalArgumentException | IllegalStateException e)
        {
            view.displayMessage("Unable to join: " + e.getMessage());
        }
    }


    /**
     * Displays the current waiting list. Uses a fresh snapshot from the model.
     */
    public void showWaitingList()
    {
        view.displayWaitingList(
            model.getWaitingRequests(),
            model.getWaitingCount());
    }


    /**
     * Calls the next student in the waiting line. Displays the served request
     * and refreshes the list.
     */
    public void callNextRequest()
    {
        try
        {
            HelpRequest request = model.callNext();

            view.displayCalledRequest(request);

            showWaitingList();
        }
        catch (NoSuchElementException e)
        {
            view.displayMessage("No students waiting.");
        }
    }


    /**
     * Collects a student ID and cancels the matching request.
     */
    public void cancelRequest()
    {
        String studentId = view.readLine("Enter the student ID to cancel: ");

        if (studentId == null)
        {
            exit();
            return;
        }

        try
        {
            boolean canceled = model.cancel(studentId);

            if (canceled)
            {
                view.displayMessage("Request successfully canceled.");
            }
            else
            {
                view.displayMessage("No matching request.");
            }

            showWaitingList();
        }
        catch (IllegalArgumentException | IllegalStateException e)
        {
            view.displayMessage("Unable to cancel: " + e.getMessage());
        }
    }


    /**
     * Stops the application.
     */
    public void exit()
    {
        running = false;
    }
}
 