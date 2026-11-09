# CS-2114---Project-3
## Prototype Overview

Our application manages a student office hours waiting line using a
linked Queue ADT. Students join with a name, student identifier, and
help topic. The TA can display the line, call the next student, and
cancel a waiting request.The MVP uses FIFO order: the first student to join is the first
student called. Student identifiers are unique among active requests.
Once a student has been called or canceled, that identifier can join
again.

The prototype uses Model–View–Controller:

Project Structure

  -HelpRequest.java – Stores information about an individual help request and validates its fields.

  -LinkedQueue.java – Implements a generic first-in, first-out queue.

  -OfficeHoursModel.java – Manages the waiting line and the rules for joining, calling, and canceling requests.
 
  -OfficeHoursView.java – Handles console input and displays menus, messages, and the waiting list.

  -OfficeHoursController.java – Handles menu choices and coordinates the Model and View.

  -OfficeHoursApp.java – Creates the application objects and starts the program.

  

# How to Run
- Java and Eclipse configured for CS 2114.
- All source files in the same project and package.
- The course-provided `student.jar` and the JUnit configuration used
  by our course for tests extending `student.TestCase`.


## Reflection

What Changed From the Specification?

Our original specification planned to separate the program into a Model, View, and Controller, with each class having a specific responsibility. We have kept that overall design.

The HelpRequest class includes validation for the student's name, student ID, and help topic. It also normalizes student IDs by trimming whitespace and converting them to lowercase. This helps prevent duplicate requests that use different capitalization or extra spaces. The class also includes an urgent field for possible priority functionality.

The OfficeHoursController follows the planned menu structure. It reads menu choices as complete lines, handles options 1 through 5, and calls the appropriate Model methods. It also handles invalid input and end-of-file situations. Validation of student information is left to the Model and HelpRequest, rather than being duplicated in the Controller.

Some of the planned functionality and testing still needs to be checked against the completed Model and View. We will update this section if our final implementation changes any method signatures, class responsibilities, or test cases.

## Did the Model Stand Alone?

Our design intended for the Model to work independently of the View. The Model should manage the waiting queue and enforce the rules for joining, calling, and canceling requests without displaying messages or reading console input.

The Controller is responsible for passing information between the Model and View. For example, it gets a waiting-list snapshot from the Model and gives that information to the View to display.

We intend to keep this separation in the final implementation. The Model should not import or depend on the View, and the View should not directly access the Model or its queue. If any implementation changes require moving responsibilities across this boundary, we will document those changes here.

# What Happened to Our Unknowns?
In our original scope, we identified four things we were unsure how to implement:

  Queue: How do we update the front and rear references after removing the final student? We still need to make sure the queue correctly          handles becoming empty after a removal.

  Traversal: How can we display the queue without removing elements? We figured this out by using a waiting-list snapshot so the requests can     be displayed without changing the actual queue.

  Duplicates: How can we detect repeated identifiers while preserving FIFO order? We plan to handle this through the Model by checking whether    a student ID is already in the waiting line before adding a new request.

  Testing: How do we verify invalid operations leave the queue unchanged? We figured this out by testing that rejected operations do not change   the waiting count or the contents and order of the waiting list.

We have figured out traversal and testing. Queue removal and duplicate detection still need to be fully verified in our final implementation.

# What still needs work
The MVP still needs to be fully integrated and tested. In particular, we need to confirm that:
- Students can join the waiting line with valid information.
- Invalid names, student IDs, and topics are rejected.
- Duplicate active student IDs are rejected.
- Students can cancel their own waiting requests.
- The next student can be called in FIFO order.
- The waiting list displays the correct requests and positions.
- The program handles an empty queue and unexpected input without crashing.
- All test files compile and pass together.

## Next Steps
Our MVP features have been implemented. Our next step is to fully test the existing functionality and make sure everything works correctly before adding new features. We plan to:
-Run all existing test files and fix any failing tests.
-Test joining, canceling, and calling students through the console.
-Test invalid names, student IDs, topics, duplicate requests, and other bad-input cases.
-Verify that the waiting list maintains FIFO order and displays the correct information.
-Confirm that the application handles an empty queue and unexpected input without crashing.

# Our change feature
For Deliverable 4, we plan to add estimated waiting times to the waiting list. The application will estimate how long each student may have to wait before receiving help. We predict this feature will affect:

OfficeHoursModel: Calculate estimated waiting times based on the number of students ahead in line and an estimated average help-session duration.

OfficeHoursView: Display the estimated waiting time next to each student's request.

OfficeHoursController: Retrieve the waiting-list information and pass it to the View.

We expect HelpRequest.java and LinkedQueue.java will not need changes because estimated waiting times can be calculated using the existing request and queue information.We will compare this prediction with the actual changes made during Deliverable 4.

# Which stretch goals will we attempt?

We plan to implement and test these additional features:

Estimated waiting times: Show students an estimate of how long they may wait before being called. The estimate will be based on the number of students ahead of them and the average time spent helping each student.

History and Statistics: Record the topics students most frequently ask for help with. This will allow teaching assistants to identify concepts students are struggling with and report those areas to the professor. We plan to track topic counts and provide a way to view the most frequently requested topics.


# What new tests will we need?

We will add tests for both new features.

Estimated waiting times:
-Calculating the estimated wait for the first student.
-Calculating estimates for students farther back in the line.
-Updating estimates when a student joins, cancels, or gets called.
-Handling an empty waiting line.
-Making sure estimates are never negative.

History and Statistics:
-Recording the topic when a student submits a help request.
-Counting how often each topic is requested.
-Identifying the most frequently requested topics.
-Treating topics with different capitalization or extra whitespace consistently.
-Confirming that statistics update correctly as new requests are added.
-Handling an empty statistics report.

#Who does what?
We will divide the remaining work among the team members:

Jacob - OfficeHoursController, estimated waiting-time integration, and related tests

Shrey - LinkedQueue review, support for new features if needed, and related tests

Manveen- HelpRequest and OfficeHoursModel updates for history and statistics, and related tests

Luke - OfficeHoursApp, OfficeHoursView updates, and integration testing

We will work together to review the final implementation and make sure both new features work correctly with the existing application.

## Attribution

| Class / Test File | Team Member | Contribution |
|---|---|---|
| `HelpRequest.java` | Manveen Sidhu | Drafted and revised |
| `HelpRequestTest.java` | Manveen Sidhu | Drafted and revised |
| `LinkedQueue.java` |  |  |
| `OfficeHoursModel.java` |  |  |
| `OfficeHoursModelTest.java` |  |  |
| `OfficeHoursView.java` | Shrey Agarwal | Used ChatGPT to help write the console interface |
| `OfficeHoursViewTest.java` |  |  |
| `OfficeHoursController.java` | Jacob Dial | Drafted and revised office hours controller class |
| `OfficeHoursControllerTest.java` | Jacob Dial | Drafted and revised office hours controller test class |
| `OfficeHoursApp.java` | Manveen Sidhu | Drafted and revised |

### GenAI Note

[Describe how your team used GenAI, including whether it helped with planning, explanations, writing code, debugging, or creating tests.]
