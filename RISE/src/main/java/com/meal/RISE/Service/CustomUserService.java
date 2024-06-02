package com.meal.RISE.Service;



import com.meal.RISE.Entity.Booking;
import com.meal.RISE.Repository.BookingRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.meal.RISE.Service.jwt.UserDetailsServiceImpl; // Import UserDetailsServiceImpl class

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomUserService {

    private final UserDetailsServiceImpl userDetailsService; // Inject UserDetailsServiceImpl
    private final BookingRepository bookingRepository;
   

    public CustomUserService(UserDetailsServiceImpl userDetailsService, BookingRepository bookingRepository) {
        this.userDetailsService = userDetailsService;
        this.bookingRepository = bookingRepository;
    }

    private Long getUserIdFromToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();

        if (principal instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) principal;
            String email = userDetails.getUsername(); // Assuming email is used as username
            UserDetails loadedUserDetails = userDetailsService.loadUserByUsername(email);
            // Assuming YourUserDetailsClass has a method to retrieve employee ID
            if (loadedUserDetails instanceof CustomUserService) {
                return ((CustomUserService) loadedUserDetails).getEmployeeId();
            } else {
                // Handle if loadedUserDetails is not an instance of YourUserDetailsClass
                return null; // or throw an exception
            }
        } else {
            // Handle if principal is not an instance of UserDetails
            return null; // or throw an exception
        }
    }

    private Long getEmployeeId() {
        return this.getUserIdFromToken();
    }




}

