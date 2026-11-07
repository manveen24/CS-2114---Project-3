import java.util.Scanner;

/**
 * Creates and starts the office hours application.
 *
 * @author Manveen Sidhu
 * @version 2026.11.07
 */
public class OfficeHoursApp
{
    /**
     * Creates the model, view, and controller, then starts the program.
     *
     * @param args
     *            unused command-line arguments
     */
    public static void main(String[] args)
    {
        OfficeHoursModel model = new OfficeHoursModel();

        Scanner input = new Scanner(System.in);
        OfficeHoursView view = new OfficeHoursView(input, System.out);

        OfficeHoursController controller =
            new OfficeHoursController(model, view);

        controller.run();
    }
}
