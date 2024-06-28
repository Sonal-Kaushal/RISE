package com.meal.RISE.Service;

import com.meal.RISE.Entity.Coupon;
import com.meal.RISE.Entity.Booking;
import com.meal.RISE.Enums.CouponStatus;
import com.meal.RISE.Repository.CouponRepository;
import com.meal.RISE.Repository.BookingRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class CouponService {

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Transactional
    public ResponseEntity<?> generateCoupon(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        // Check if a coupon has already been generated for this booking
        Coupon existingCoupon = booking.getCoupon();
        if (existingCoupon != null) {
            String errorMessage = "Coupon has already been generated for this booking";
            System.out.println(errorMessage);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", errorMessage);
            return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
        }

        // Create a new coupon
        Coupon newCoupon = new Coupon();
        newCoupon.setCouponId(generateUniqueCouponId());
        newCoupon.setExpirationTime(LocalDateTime.now().plusSeconds(30));
        newCoupon.setStatus(CouponStatus.ACTIVE);
        newCoupon.setBooking(booking);
        booking.setCoupon(newCoupon);

        couponRepository.save(newCoupon);

        Map<String, String> response = new HashMap<>();
        response.put("couponId", newCoupon.getCouponId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    @Transactional
    public void redeemCoupon(String couponId) {
        Coupon coupon = couponRepository.findByCouponId(couponId);
        if (coupon != null && coupon.getExpirationTime().isAfter(LocalDateTime.now())) {
            coupon.setStatus(CouponStatus.REDEEMED);
            coupon.setExpirationTime(LocalDateTime.now().plusSeconds(5)); // Set expiration time to 5 seconds after redemption
            couponRepository.save(coupon);
            System.out.println("Coupon redeemed. New expiration time: " + coupon.getExpirationTime());

            // Verify the status change
            Coupon updatedCoupon = couponRepository.findByCouponId(couponId);
            System.out.println("Updated coupon status: " + updatedCoupon.getStatus());
        } else {
            if (coupon == null) {
                System.out.println("Coupon not found");
            } else {
                System.out.println("Coupon expired. Current time: " + LocalDateTime.now() + " Expiration time: " + coupon.getExpirationTime());
            }
            throw new RuntimeException("Coupon is either expired or does not exist");
        }
    }

    public void deleteCoupon(String couponId) {
        Coupon coupon = couponRepository.findByCouponId(couponId);
        if (coupon != null) {
            couponRepository.delete(coupon);
        }
    }

    private String generateUniqueCouponId() {
        String couponId;
        do {
            couponId = UUID.randomUUID().toString().replaceAll("-", "").substring(0, 16);
        } while (couponRepository.findByCouponId(couponId) != null);
        return couponId;
    }

    @Scheduled(fixedRate = 1000) // Check every second
    public void deleteExpiredCoupons() {
        LocalDateTime now = LocalDateTime.now();
        List<Coupon> expiredCoupons = couponRepository.findByExpirationTimeBefore(now);
        for (Coupon coupon : expiredCoupons) {
            System.out.println("Deleting expired coupon with ID: " + coupon.getCouponId());
            couponRepository.delete(coupon);
        }
    }


}
