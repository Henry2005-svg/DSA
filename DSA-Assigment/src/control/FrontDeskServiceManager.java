package control;

import adt.BinarySearchTree;
import entity.Booking;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class FrontDeskServiceManager {

    // Store bookings using a Binary Search Tree
    private BinarySearchTree<Booking> bookingTree;

    public FrontDeskServiceManager() {

        // Create an empty BST
        bookingTree = new BinarySearchTree<>();
    }

    public Booking findBookingByConfirmationCode(
            String confirmationCode) {

        // Check whether the confirmation code is valid
        if (!isValidConfirmationCode(confirmationCode)) {
            return null;
        }

        // Create a temporary booking containing
        // the confirmation code we want to search
        Booking searchBooking = new Booking(
                "",
                confirmationCode,
                "",
                "",
                "",
                "",
                "",
                0.0,
                ""
        );

        // Search the BST
        return bookingTree.search(searchBooking);
    }

    public boolean isValidConfirmationCode(
            String confirmationCode) {

        // Confirmation number must contain exactly 8 digits
        return confirmationCode != null
                && confirmationCode.matches("\\d{8}");
    }

    public void loadBookingsFromFile(String fileName) {

        try (BufferedReader reader =
                     new BufferedReader(
                             new FileReader(fileName))) {

            String line;

            while ((line = reader.readLine()) != null) {

                // Skip empty lines
                if (line.trim().isEmpty()) {
                    continue;
                }

                // Separate booking information
                String[] data = line.split(",");

                // Each booking must contain 9 fields
                if (data.length != 9) {
                    continue;
                }

                String bookingId = data[0].trim();
                String confirmationCode = data[1].trim();
                String guestName = data[2].trim();
                String roomNumber = data[3].trim();
                String roomType = data[4].trim();
                String checkInDate = data[5].trim();
                String checkOutDate = data[6].trim();

                double bookingAmount =
                        Double.parseDouble(
                                data[7].trim());

                String status = data[8].trim();

                // Create booking object
                Booking booking = new Booking(
                        bookingId,
                        confirmationCode,
                        guestName,
                        roomNumber,
                        roomType,
                        checkInDate,
                        checkOutDate,
                        bookingAmount,
                        status
                );

                // Add booking into the BST
                bookingTree.add(booking);
            }

        } catch (IOException e) {

            System.out.println(
                    "Error loading bookings: "
                    + e.getMessage());
        }
    }
}