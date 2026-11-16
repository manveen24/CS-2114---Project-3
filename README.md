# Project 3 — Office Hours Queue
## Deliverable 4: Second Prototype + Tests

## Prototype Overview

Our application manages a student office hours waiting line using a linked Queue ADT. Students submit their name, student identifier, and help topic. The TA can display the waiting line, call the next student, and cancel a waiting request.

Student identifiers must be unique among active requests. Once a request has been served or canceled, that identifier may join again. Names, identifiers, and topics are validated before a request is accepted.

Our second prototype implements two stretch goals:

- **Priority queue:** Higher-priority requests are served before regular requests. Requests at the same priority level maintain FIFO order.
- **Estimated waiting times:** The waiting list displays estimated waits based on the current service order and an estimated help-session duration.

Support for multiple TAs is our remaining feature for the final build.

## Project Structure

The application follows Model–View–Controller.

| Class | Responsibility |
| --- | --- |
| `HelpRequest` | Stores an immutable, validated help request, including its priority status. |
| `LinkedQueue<T>` | Stores requests using linked nodes and provides the queue operations used by the Model. |
| `OfficeHoursModel` | Owns the waiting line, enforces request rules, manages priority ordering, and calculates waiting estimates. |
| `OfficeHoursView` | Reads console input and formats menus, requests, estimated waits, and messages. |
| `OfficeHoursController` | Routes user actions to the Model and passes current results to the View. |
| `OfficeHoursApp` | Creates the application objects and starts the Controller. |

The private nested `Node` class inside `LinkedQueue` stores an entry and its next reference.

## How to Compile and Run

### Setup

1. Open or import the Java project in Eclipse.
2. Use the JDK configured for CS 2114.
3. Keep source and test files in their declared package.
4. Add JUnit 5 through **Build Path → Add Libraries → JUnit → JUnit 5**.
5. Include any course-provided libraries required by the submitted source files.
6. Select **Project → Build Project** and confirm that the project compiles without errors.

### Run the Application

1. Right-click `OfficeHoursApp.java`.
2. Select **Run As → Java Application**.
3. Use the console menu to submit requests, display the waiting line, call students, cancel requests, and access priority handling.
4. Display the waiting line to see the current service order and estimated waiting times.

Waiting times are estimates, not guaranteed appointment times.

### Run the Tests

Right-click the project or test package and select **Run As → JUnit Test**. To run an individual test file, right-click that file and select **Run As → JUnit Test**.

The tests use JUnit 5. Model and Queue tests construct those objects directly and do not require the View, console input, or `OfficeHoursApp`.

**Complete-suite result:** [Enter the actual number of tests run, failures, and errors.]

## Reflection

### How Did Our Design Absorb the Change?

Our D3 prediction named estimated waiting times as our change feature. We predicted changes to `OfficeHoursModel`, `OfficeHoursController`, and `OfficeHoursView`, while `HelpRequest` and `LinkedQueue` would remain unchanged for that feature.

Estimated waiting times fit the existing MVC structure. The Model calculates estimates, the Controller retrieves them, and the View displays them. A waiting estimate is derived from the current service order rather than stored permanently in a request. This prevents a request from keeping an outdated estimate after another student joins, is served, or cancels.

We also implemented priority handling. This adds service-order logic beyond our original FIFO MVP. FIFO still applies within each priority level.

The following comparison separates the two features so that changes made for priority handling are not incorrectly attributed to waiting estimates.

| File | D3 waiting-time prediction | Actual waiting-time change | Priority-related change |
| --- | --- | --- | --- |
| `OfficeHoursModel.java` | Change | Calculates estimates from the current service order. | Manages priority rules and service order. |
| `OfficeHoursController.java` | Change | Retrieves estimates and passes them to the View. | Routes priority-related user actions to the Model. |
| `OfficeHoursView.java` | Change | Displays estimated waiting times. | Displays priority information and supported priority actions. |
| `HelpRequest.java` | Unchanged | No waiting-time calculation or stored estimate is needed. | Its existing urgency field represents request priority. [Confirm whether this file changed after D3.] |
| `LinkedQueue.java` | Unchanged | No waiting-time calculation is needed in the Queue. | [State the actual priority-related changes, or “Unchanged.”] |
| `OfficeHoursApp.java` | Unchanged | No startup changes were needed. | [Confirm whether unchanged.] |

**Prediction comparison:** [Confirm which files actually changed against the D3 version. If a file predicted to remain unchanged changed, explain the reason here.]

The main insight was that both priority order and waiting estimates belong in the Model. The View can display different results without taking responsibility for the rules that produce them.

### Did the Change Cross the Model/View Line?

The new information passes through the existing MVC boundary, but business logic remains in the Model.

The Model manages the waiting line, determines service order, and calculates estimates. It does not read console input, print messages, or call the View.

The Controller receives user actions, calls the appropriate Model operations, and retrieves fresh display information. The View formats the information supplied by the Controller.

The View now knows how to display priority and estimated minutes. It does not need to know how linked nodes work or how the Model determines service order.

The View keeps no persistent copy of the waiting queue, request count, priority assignments, or waiting estimates. It uses the data supplied for the current display call. After a state-changing action, the Controller retrieves current information again.

### What Changed From Our D3 Plan?

Estimated waiting times were implemented as planned. We also implemented priority handling, which expands the original FIFO behavior.

Our earlier description stated that the first student to join would always be the first called. The updated rule is that requests are served by priority, with FIFO order preserved within the same priority level.

Waiting estimates follow the actual service order, including priority changes. They must therefore be refreshed when an action changes that order.

We shifted our remaining stretch-goal work from History and Statistics to multiple-TA support. We are prioritizing the ability to represent several TAs helping students during the same session.

We also expanded testing to include priority ordering, waiting estimates, and Queue order after interleaved operations. Our test instructions now describe JUnit 5, matching the annotations and assertions used in our tests.

### Testing and Bad Input

Our Model and Queue tests run independently of the View. They check returned results and observable state, including waiting count, request order, and estimates.

| Area | Normal cases | Invalid-input or boundary cases |
| --- | --- | --- |
| Request validation | Valid fields, trimming, and identifier normalization. | Null, blank, overlength, and control-character input. |
| Joining | Accept a valid request and update the waiting line. | Reject duplicate active identifiers, including differences in capitalization or surrounding spaces. |
| Calling | Remove the next request according to priority and FIFO rules. | Handle an empty waiting line according to the Model contract. |
| Cancellation | Remove the selected request while preserving the order of others. | Handle invalid or missing identifiers without changing valid requests. |
| Queue insertion and removal | Maintain correct order and size. | Handle invalid entries and empty removal according to the Queue contract. |
| Queue peek | Return the front entry without removing it. | Handle an empty queue according to its contract. |
| Queue boundaries | Remove the final entry, then successfully add another. | Verify that empty-state front and rear references do not break later operations. |
| Priority | Serve higher-priority requests first and preserve FIFO within a priority level. | Reject invalid priority actions without changing valid queue state. |
| Waiting estimates | Calculate estimates for the first and later requests in service order. | Handle an empty line and ensure estimates are never negative. |
| State changes | Refresh estimates after joining, calling, cancellation, and priority changes. | Rejected operations leave request order, count, and estimates unchanged. |
| Console input | Accept supported menu choices. | Handle nonnumeric input, unsupported choices, and end-of-input without an unhandled exception. |

Each core Model and Queue method has a normal test and an applicable bad-input or boundary test. Methods without input parameters are checked in relevant empty and nonempty states.

We specifically test interleaved operations:

1. Enqueue A and B.
2. Dequeue A.
3. Enqueue C.
4. Verify that the remaining removal order is B, then C.

Additional priority tests verify that interleaved operations preserve FIFO order among requests with equal priority.

Every scoped bad-input case has a corresponding automated test. Console-input tests supplement the independent Model and Queue tests.

**Before submission:** [Confirm that all coverage described above exists and that the complete suite executes successfully. Correct any claim that does not match the submitted tests.]

### What Does Not Work Yet?

Multiple-TA support is not complete. We still need to track TA availability, assign students to available TAs, and account for simultaneous help sessions when calculating waiting estimates.

Current estimates use an assumed help-session duration. Actual help sessions may take more or less time.

**Other known defects, missing tests, or failing tests:** [List actual issues, or write “None identified in our completed testing” if verified.]

### What Design Change Would Make the Next Feature Cheaper?

A Model-owned representation of TA availability and current assignments would make multiple-TA support easier to implement. Assignment and completion operations could update that state in one place, and waiting-time calculations could use it directly.

The Controller would route assignment and completion actions. The View would receive a snapshot of TA status and waiting information without accessing internal collections.

Keeping the waiting-time calculation in one Model method would also let us replace the single-TA calculation without duplicating logic across the application.

## Next Steps

### Remaining Work for the Final Build

| Order | Work | Owner |
| --- | --- | --- |
| 1 | Define rules for TA availability, assignment, and completion of a help session. | All members |
| 2 | Add Model state and operations for multiple TAs, including validation and independent tests. | Manveen |
| 3 | Verify that priority and FIFO ordering remain correct as available TAs receive students. | Shrey |
| 4 | Integrate TA actions through the Controller and update waiting-estimate retrieval. | Jacob |
| 5 | Display TA availability and assignments in the View and complete application integration. | Luke |
| 6 | Run the complete regression suite and fix remaining failures. | All members |
| 7 | Update the README, verify attribution, and rehearse the final demonstration. | All members |

### Stretch Goals: Committed and Dropped

| Feature | Status | Remaining work |
| --- | --- | --- |
| Priority queue | Implemented | Regression testing of priority rules and FIFO order within each priority level. |
| Estimated waiting times | Implemented | Regression testing and adaptation for multiple TAs. |
| Multiple TAs | Committed for the final build | Availability tracking, assignments, completion handling, updated estimates, and tests. |
| History and Statistics | Dropped from the final-build scope | No further implementation planned. |

### Timed Three-Minute Demo

| Time | Demonstration |
| --- | --- |
| 0:00–0:20 | Introduce the application and briefly explain Model, View, and Controller responsibilities. |
| 0:20–0:50 | Add regular requests and display their FIFO order and estimated waits. |
| 0:50–1:20 | Use the implemented priority action and show the updated service order and estimates. |
| 1:20–1:45 | Call the next student and display the refreshed waiting line. |
| 1:45–2:05 | Cancel a request and show the updated order and estimates. |
| 2:05–2:25 | Demonstrate duplicate and invalid input without crashing or changing valid queue data. |
| 2:25–2:50 | Show passing tests for independent Model behavior, priority ordering, waiting estimates, and interleaved Queue operations. |
| 2:50–3:00 | Identify the classes changed and explain the remaining multiple-TA work. |

## Attribution

| Class / Test File | Team Member | Contribution |
|---|---|---|
| `HelpRequest.java` | Manveen Sidhu | Drafted and revised |
| `HelpRequestTest.java` | Manveen Sidhu | Drafted and revised |
| `LinkedQueue.java` | Luke Stabler | Drafted and Revised |
| `OfficeHoursModel.java` | Luke Stabler | Drafted and Revised |
| `OfficeHoursModelTest.java` | Luke Stabler | Drafted and Revised |
| `OfficeHoursView.java` | Shrey Agarwal | Used ChatGPT to help write the console interface |
| `OfficeHoursViewTest.java` | Shrey Agarwal | Used ChatGPT to help write the console interface |
| `OfficeHoursController.java` | Jacob Dial | Drafted and revised office hours controller class |
| `OfficeHoursControllerTest.java` | Jacob Dial | Drafted and revised office hours controller test class |
| `OfficeHoursApp.java` | Manveen Sidhu | Drafted and revised |
### GenAI Note

We used ChatGPT to help review our MVC design, suggest normal and bad-input test cases, assist with portions of `HelpRequest` and its tests, and draft README wording. We also requested guidance about implementing and testing stretch goals.
