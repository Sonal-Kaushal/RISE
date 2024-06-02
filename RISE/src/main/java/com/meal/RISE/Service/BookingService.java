package com.meal.RISE.Service;

import com.meal.RISE.DTO.BookingRequestDto;
import com.meal.RISE.DTO.QuickBookingRequestDto;
import com.meal.RISE.DTO.SingleBookingRequestDto;
import com.meal.RISE.Entity.Booking;
import com.meal.RISE.Entity.Employee;
import com.meal.RISE.Entity.Notification;
import com.meal.RISE.Enums.MealType;
import com.meal.RISE.Enums.Status;
import com.meal.RISE.Repository.BookingRepository;
import com.meal.RISE.Repository.EmployeeRepository;
import com.meal.RISE.Service.jwt.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private NotificationService notificationService;

    private final UserDetailsServiceImpl userDetailsService;

    private static final LocalTime BOOKING_CUTOFF_TIME = LocalTime.of(20, 0);

//    public BookingService(UserDetailsServiceImpl userDetailsService) {
//        this.userDetailsService = userDetailsService;
//    }

    public Booking quickBookMeal(QuickBookingRequestDto requestDto) throws Exception {
        Long userId = requestDto.getUserId();
        MealType mealType = requestDto.getMealType();

        LocalDate tomorrow = LocalDate.now().plusDays(1);

        // Check if tomorrow is a weekend
        if (tomorrow.getDayOfWeek() == DayOfWeek.SATURDAY || tomorrow.getDayOfWeek() == DayOfWeek.SUNDAY) {
            throw new Exception("Booking cannot be made for weekends");
        }

        // Check if the current time is after the booking cutoff time
        if (LocalTime.now().isAfter(BOOKING_CUTOFF_TIME)) {
            throw new Exception("Booking cannot be made after " + BOOKING_CUTOFF_TIME);
        }

        validateBookingTime(tomorrow);

        Employee employee = employeeRepository.findById(userId)
                .orElseThrow(() -> new Exception("Employee not found"));

        Booking booking = bookMeal(employee.getId(), tomorrow, tomorrow, mealType);

        triggerBookingNotification(userId, tomorrow);

        return booking;
    }

    public Booking bookSingleMeal(SingleBookingRequestDto requestDto) throws Exception {
        Long userId = requestDto.getUserId();
        LocalDate startDate = requestDto.getStartDate();
        LocalDate endDate = requestDto.getEndDate();
        MealType mealType = requestDto.getMealType();
        LocalDate today = LocalDate.now();
        if (startDate.isBefore(today.plusDays(2))) {
            throw new IllegalArgumentException("Bookings cannot be made for today or yesterday.");
        }
        if (LocalTime.now().isAfter(BOOKING_CUTOFF_TIME)) {
            throw new Exception("Booking cannot be made after " + BOOKING_CUTOFF_TIME);
        }
        LocalDate threeMonthsLater = LocalDate.now().plusMonths(3);
        if (startDate.isAfter(threeMonthsLater)) {
            throw new IllegalArgumentException("Bookings cannot be made more than 3 months in advance.");
        }
        validateDateRange(startDate, endDate);

        List<Booking> bookings = new ArrayList<>();
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            if (!isWeekend(date)) {
                List<Booking> existingBookings = bookingRepository.findByEmployeeIdAndStartDate(userId, date);
                if (existingBookings.isEmpty()) {
                    try {
                        Booking booking = bookMeal(userId, date, date, mealType);
                        bookings.add(booking);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    throw new IllegalArgumentException("User already has a booking for the specified date: " + date);
                }
            }
        }

        if (!bookings.isEmpty()) {
            triggerBookingNotification(userId, startDate);
            return bookingRepository.save(bookings.get(0)); // Return the first booking, assuming only one booking is made
        } else {
            throw new IllegalArgumentException("No bookings made.");
        }
    }

    public List<Booking> bookMeals(BookingRequestDto requestDto) throws Exception {
        Long userId = requestDto.getUserId();
        LocalDate startDate = requestDto.getStartDate();
        LocalDate endDate = requestDto.getEndDate();
        MealType mealType = requestDto.getMealType();

        LocalDate today = LocalDate.now();
        if (startDate.isBefore(today.plusDays(2))) {
            throw new IllegalArgumentException("Bookings cannot be made for today or yesterday.");
        }
        if (LocalTime.now().isAfter(BOOKING_CUTOFF_TIME)) {
            throw new Exception("Booking cannot be made after " + BOOKING_CUTOFF_TIME);
        }
        LocalDate threeMonthsLater = LocalDate.now().plusMonths(3);
        if (startDate.isAfter(threeMonthsLater)) {
            throw new IllegalArgumentException("Bookings cannot be made more than 3 months in advance.");
        }

        Employee employee = employeeRepository.findById(userId)
                .orElseThrow(() -> new Exception("Employee not found"));

        validateDateRange(startDate, endDate);

        List<Booking> bookings = new ArrayList<>();
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            if (!isWeekend(date)) {
                List<Booking> existingBookings = bookingRepository.findByEmployeeIdAndStartDate(userId, date);
                if (existingBookings.isEmpty()) {
                    try {
                        Booking booking = bookMeal(employee.getId(), date, date, mealType);
                        bookings.add(booking);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    throw new IllegalArgumentException("User already has a booking for the specified date: " + date);
                }
            }
        }

        if (!bookings.isEmpty()) {
            triggerBookingNotification(userId, startDate, endDate);
            return bookingRepository.saveAll(bookings);
        } else {
            throw new IllegalArgumentException("No bookings made.");
        }
    }

    private void validateDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Both start date and end date must be provided");
        }

        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date cannot be after end date");
        }

        if (startDate.isBefore(LocalDate.now()) || endDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Booking dates cannot be in the past");
        }
    }

    private void validateBookingTime(LocalDate bookingDate) throws Exception {
        if (LocalTime.now().isAfter(BOOKING_CUTOFF_TIME)) {
            throw new Exception("Booking cannot be made after " + BOOKING_CUTOFF_TIME);
        }
        if (isWeekend(bookingDate)) {
            throw new Exception("Booking cannot be made for weekends.");
        }
    }

    private boolean isWeekend(LocalDate date) {
        return date.getDayOfWeek().getValue() >= 6;
    }

    private Booking bookMeal(Long userId, LocalDate startDate, LocalDate endDate, MealType mealType) throws Exception {
        LocalDate today = LocalDate.now();
        if (startDate.isBefore(today) || endDate.isBefore(today)) {
            throw new IllegalArgumentException("Booking dates cannot be in the past");
        }

        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("End date must be after start date");
        }

        Optional<Object> existingBookings = bookingRepository.findByEmployeeIdAndStartDateBetween(userId, startDate, endDate);
        if (existingBookings.isPresent()) {
            throw new IllegalArgumentException("Meal already booked for this date range");
        }

        Employee employee = employeeRepository.findById(userId)
                .orElseThrow(() -> new Exception("Employee not found"));

        Booking booking = new Booking();
        booking.setEmployee(employee);
        booking.setStartDate(startDate);
        booking.setEndDate(endDate);
        booking.setMealType(mealType);
        booking.setStatus(Status.CONFIRMED);

        return bookingRepository.save(booking);
    }

    private void triggerBookingNotification(Long userId, LocalDate startDate) {
        String message = "Booking made for " + startDate ;

        Notification notification = new Notification();
        notification.setUserId(String.valueOf(userId));
        notification.setMessage(message);

        notificationService.sendNotification(notification);
    }

    private void triggerBookingNotification(Long userId, LocalDate startDate, LocalDate endDate) {
        String message = "Bulk Booking made from " + startDate + " to " + endDate;

        Notification notification = new Notification();
        notification.setUserId(String.valueOf(userId));
        notification.setMessage(message);

        notificationService.sendNotification(notification);
    }

//    public String cancelBooking(Long bookingId) {
//        Booking booking = bookingRepository.findById(bookingId).orElse(null);
//        if (booking == null) {
//            return "Booking not found.";
//        }
//
//        // Set the booking status to cancelled
//        booking.setStatus(Status.CANCELLED);
//        bookingRepository.save(booking);
//
//        // Trigger cancellation notification
//        triggerCancellationNotification(booking.getEmployee().getId(), "Booking canceled successfully.");
//
//        return "Booking canceled successfully.";
//    }



    public ResponseEntity<Map<String, String>> cancelBooking(LocalDate cancellationDate) throws Exception {
        // Find bookings with the given cancellation date
        List<Booking> bookingsToCancel = bookingRepository.findByStartDate(cancellationDate);

        if (bookingsToCancel.isEmpty()) {
            // Return a JSON response indicating no bookings found
            Map<String, String> response = new HashMap<>();
            response.put("message", "No bookings found for the cancellation date.");
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }
        LocalTime cancelCutoffTime = LocalTime.of(22, 0); // 10 PM
        if (LocalTime.now().isAfter(cancelCutoffTime)) {
            throw new Exception("Cancellation cannot be done after 10 PM.");
        }

        // Cancel each booking
        for (Booking booking : bookingsToCancel) {
            // Set the booking status to cancelled
            booking.setStatus(Status.CANCELLED);
            bookingRepository.save(booking);

            // Trigger cancellation notification
            triggerCancellationNotification(booking.getEmployee().getId(), "Booking canceled successfully." + cancellationDate);
        }

        // Return a JSON response indicating successful cancellation
        Map<String, String> response = new HashMap<>();
        response.put("message", "Bookings canceled successfully." + cancellationDate);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }



    private void triggerCancellationNotification(Long userId, String message) {
        Notification notification = new Notification();
        notification.setUserId(String.valueOf(userId));
        notification.setMessage(message);
        notificationService.sendNotification(notification);
    }




    @Autowired
    public BookingService(BookingRepository bookingRepository, UserDetailsServiceImpl userDetailsService) {
        this.bookingRepository = bookingRepository;
        this.userDetailsService = userDetailsService;
    }

//    public List<LocalDate> getBookingDatesForUser(Long userId) {
//        // Find the employee by userId
//        Employee user = employeeRepository.findById(userId)
//                .orElseThrow(() -> new IllegalArgumentException("User not found"));
//
//        // Retrieve bookings associated with the user
//        List<Booking> bookings = bookingRepository.findByEmployee(user);
//
//        // Extract booking dates from the bookings
//        List<LocalDate> bookingDates = new ArrayList<>();
//        for (Booking booking : bookings) {
//            // Add each booking date to the list
//            LocalDate startDate = booking.getStartDate();
//            LocalDate endDate = booking.getEndDate();
//
//            // Add all dates between start date and end date (inclusive)
//            while (!startDate.isAfter(endDate)) {
//                bookingDates.add(startDate);
//                startDate = startDate.plusDays(1);
//            }
//        }
//
//        return bookingDates;
//    }
public List<LocalDate> getBookingDatesForUser(Long userId) {
    // Find the employee by userId
    Employee user = employeeRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

    // Retrieve bookings associated with the user and filter only active bookings
    List<Booking> bookings = bookingRepository.findByEmployeeAndStatus(user, Status.CONFIRMED);

    // Extract booking dates from the bookings
    List<LocalDate> bookingDates = new ArrayList<>();
    for (Booking booking : bookings) {
        // Add each booking date to the list
        LocalDate startDate = booking.getStartDate();
        LocalDate endDate = booking.getEndDate();

        // Add all dates between start date and end date (inclusive)
        while (!startDate.isAfter(endDate)) {
            bookingDates.add(startDate);
            startDate = startDate.plusDays(1);
        }
    }

    return bookingDates;
}


}



