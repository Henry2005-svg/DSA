# TAR UMT Resort Management System

BMCS2063 console application using Entity-Control-Boundary architecture.
Module 2 implements Front-Desk Service and Loyalty Rewards. Module 3 implements
Housekeeping and Task Log. Module 1 is a teammate placeholder.

## Packages

- `entity`: `RoomStatus`, `Room`, `HousekeepingStaff`, `HousekeepingTask`,
  `StatusChange`, `Booking`, `LoyaltyMember`, `LoyaltyTier`
- `adt`: generic linked-node `StackInterface<T>`, `Node<T>`,
  `LinkedStack<T>`, `BinarySearchTreeInterface<K,V>`,
  `BinarySearchTree<K,V>`, `BinaryTreeNode<K,V>`
- `control`: `HousekeepingManager`, `HousekeepingReportManager`,
  `FrontDeskServiceManager`, `FrontDeskReportManager`
- `boundary`: shared menu, front-desk menus, housekeeping menus, and walk-in
  placeholder
- `client`: `ResortManagementApplication`

## Front-Desk Service

Author: Eason San Chee Hau

The front-desk module uses a custom non-linear ADT for fast confirmation-code
searching. Booking records are inserted into a custom
`BinarySearchTree<String, Booking>`, where the key is the 8-digit confirmation
code. This lets the front desk retrieve booking details without scanning every
booking one by one.

Main functions:

- Fast 8-digit confirmation lookup by Binary Search Tree
- Display all bookings and loyalty members
- Record additional guest spending and recalculate loyalty points
- Auto-upgrade loyalty tier from Silver to Gold to Platinum
- Redeem reward points for room upgrade, breakfast voucher, or late check-out
- Clean up expired points based on expiry date
- Generate loyalty tier distribution and points audit report
- Generate high-value guest report sorted by total spending

## Stack ADT

`HousekeepingManager` stores status changes in one custom
`LinkedStack<StatusChange>`. Every update pushes the actual old and new status.
Undo pops the newest record and restores its `previousStatus`, following LIFO.
`push`, `pop`, and `peek` are O(1).

## Algorithms

Front-desk confirmation lookup uses a custom Binary Search Tree. Average-case
lookup is O(log n), and worst-case lookup is O(n) if the tree becomes heavily
unbalanced. Loyalty member lookup uses manual linear search: O(n). The
high-value guest report uses a custom merge sort by total spending: O(n log n).

Room, task, and staff searches are manual linear searches: O(n). Display and
report copies are sorted with stable insertion sort: O(n^2) worst case. No Java
Collections Framework storage, built-in sorting, or Stream API is used.

## Front-Desk Menu Guide

From the main menu, enter `2` for Front-Desk Service.

- `1`: Display all bookings or loyalty members
- `2`: Search booking by 8-digit confirmation code
- `3`: Record spending, add points, and auto-upgrade member tier
- `4`: Redeem loyalty rewards
- `5`: Clean expired loyalty points
- `6`: Generate front-desk reports
- `0`: Return to main menu

Sample confirmation codes:

- `12345678`
- `23456789`
- `34567890`
- `45678901`
- `56789012`

## Terminal Commands

Run in PowerShell:

```powershell
cd "C:\Users\User\OneDrive\Documents\GitHub\DSA\DSA-Assigment"
$files = Get-ChildItem ".\src" -Recurse -Filter *.java
javac -encoding UTF-8 -source 8 -target 8 -d ".\build\classes" $files.FullName
java -cp ".\build\classes" client.ResortManagementApplication
```

## Text-File Storage

All records are stored in the `data` folder:

- `bookings.txt`: `confirmationCode|memberId|guestName|roomNumber|roomType|checkInDate|checkOutDate|bookingAmount|status`
- `members.txt`: `memberId|guestName|totalSpent|pointsBalance|totalPointsEarned|totalPointsRedeemed|expiredPoints|pointsExpiryDate`
- `rooms.txt`: `roomNumber|roomType|status|lateCheckout`
- `staff.txt`: `staffId|staffName|available`
- `tasks.txt`: `taskId|roomNumber|staffId|staffName|description|status|completed|active|cancelled`
- `status_history.txt`: `roomNumber|previousStatus|newStatus|updateDateTime|reason`

The application loads these files at startup and saves them after every
successful modification. You may edit them in a text editor while the program
is closed. Keep the field order and `|` separators unchanged, and restart the
application after manual edits.
