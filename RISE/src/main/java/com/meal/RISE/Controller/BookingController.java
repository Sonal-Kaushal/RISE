package com.meal.RISE.Controller;

import com.meal.RISE.DTO.BookingRequestDto;
import com.meal.RISE.DTO.QuickBookingRequestDto;
import com.meal.RISE.DTO.SingleBookingRequestDto;
import com.meal.RISE.Entity.Booking;
import com.meal.RISE.Repository.BookingRepository;
import com.meal.RISE.Service.BookingService;
import com.meal.RISE.Service.CustomUserService;
import com.meal.RISE.Service.NotificationService;
import jakarta.validation.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {



    @Autowired
    private BookingService bookingService;

    @Autowired

    private NotificationService notificationService;


    @PostMapping("/quickBook")
    public ResponseEntity<?> quickBookMeal(@RequestBody QuickBookingRequestDto request) {
        try {
            Booking booking = bookingService.quickBookMeal(request);
            return ResponseEntity.ok(booking);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

@PostMapping("/bulk")
public ResponseEntity<?> bookMeals(@RequestBody BookingRequestDto request) {
    try {
        List<Booking> bookings = (List<Booking>) bookingService.bookMeals(request);
        return ResponseEntity.ok(bookings);
    } catch (Exception e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}

    @PostMapping("/single")
    public ResponseEntity<?> bookSingleMeal(@RequestBody SingleBookingRequestDto request) {
        try {
            Booking booking = bookingService.bookSingleMeal(request);
            return ResponseEntity.ok(booking);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/cancel")
    public ResponseEntity<Map<String, String>> cancelBooking(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate cancellationDate) {
        try {
            return bookingService.cancelBooking(cancellationDate);
        } catch (Exception e) {

            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap("message", "An error occurred: " + e.getMessage()));
        }
    }


    @GetMapping("/dates/{userId}")
    public List<LocalDate> getBookingDatesForUser(@PathVariable Long userId) {
        return bookingService.getBookingDatesForUser(userId);
    }

}
