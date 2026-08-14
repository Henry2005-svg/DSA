# TAR UMT Resort Management System

BMCS2063 console application using Entity-Control-Boundary architecture.
Modules 1 and 2 are placeholders; Module 3 implements Housekeeping and Task Log.

## Packages

- `entity`: `RoomStatus`, `Room`, `HousekeepingStaff`, `HousekeepingTask`, `StatusChange`
- `adt`: generic linked-node `StackInterface<T>`, `Node<T>`, `LinkedStack<T>`
- `control`: `HousekeepingManager`, `HousekeepingReportManager`
- `boundary`: shared menu, housekeeping menus, and two teammate placeholders
- `client`: `ResortManagementApplication`

## Stack ADT

`HousekeepingManager` stores status changes in one custom
`LinkedStack<StatusChange>`. Every update pushes the actual old and new status.
Undo pops the newest record and restores its `previousStatus`, following LIFO.
`push`, `pop`, and `peek` are O(1).

## Algorithms

Room, task, and staff searches are manual linear searches: O(n). Display and
report copies are sorted with stable insertion sort: O(n²) worst case. No Java
Collections Framework storage, built-in sorting, or Stream API is used.

## Terminal commands

Run in PowerShell:

```powershell
cd "D:\DSA Assigment"
$files = Get-ChildItem ".\src" -Recurse -Filter *.java
& "D:\JDK redhat\bin\javac.exe" -encoding UTF-8 -source 8 -target 8 -d ".\build\classes" $files.FullName
& "D:\JDK redhat\bin\java.exe" -cp ".\build\classes" client.ResortManagementApplication
```

## Text-file storage

All records are stored in the `data` folder:

- `rooms.txt`: `roomNumber|roomType|status|lateCheckout`
- `staff.txt`: `staffId|staffName|available`
- `tasks.txt`: `taskId|roomNumber|staffId|staffName|description|status|completed|active|cancelled`
- `status_history.txt`: `roomNumber|previousStatus|newStatus|updateDateTime|reason`

The application loads these files at startup and saves them after every
successful modification. You may edit them in a text editor while the program
is closed. Keep the field order and `|` separators unchanged, and restart the
application after manual edits.
