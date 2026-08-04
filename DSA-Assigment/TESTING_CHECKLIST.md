# Testing Checklist

- Enter invalid, alphabetic, blank, and out-of-range menu values.
- Open both teammate placeholder modules and return to the shared menu.
- Display all rooms, staff, and tasks; verify totals and short status forms.
- Search existing and missing room numbers, task IDs, and staff IDs.
- Reject duplicate/blank task IDs, missing rooms, busy staff, and rooms with active tasks.
- Add a task to a dirty room; verify the status becomes CIP and staff becomes Assigned.
- Change task description and staff; verify both staff availability values.
- Reject completion before RFC; complete an RFC task and verify staff is released.
- Cancel an active task with confirmation and verify its record remains.
- Test every normal status transition and reject skipped/backward transitions.
- Record failed inspection only from INS.
- Record late checkout and verify applicable active cleaning status returns to D.
- Undo twice and verify each actual previous status is restored in LIFO order.
- Generate both reports using every filter and verify their summaries.
- Confirm displays are sorted by room number, staff ID, or task ID.
- Recompile and scan for prohibited collection, sort, and stream APIs.

- "D:\JDK redhat\bin\java.exe" -cp bin client.ResortManagementApplication