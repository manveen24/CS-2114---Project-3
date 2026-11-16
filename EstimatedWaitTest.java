import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class EstimatedWaitTest {

    @Test
    public void testEstimatedWait() {
        OfficeHoursModel model = new OfficeHoursModel();

        model.join("Jacob", "001", "Java");
        assertEquals(0, model.getEstimatedWaitMinutes(1));

        model.join("Alex", "002", "Queues");
        assertEquals(30, model.getEstimatedWaitMinutes(2));

        model.join("Luke", "003", "Recursion");
        assertEquals(60, model.getEstimatedWaitMinutes(3));
    }

    @Test
    public void testWaitUpdatesAfterServing() {
        OfficeHoursModel model = new OfficeHoursModel();

        model.join("Jacob", "001", "Java");
        model.join("Alex", "002", "Queues");

        model.callNext();

        assertEquals(0, model.getEstimatedWaitMinutes(1));
    }
}
