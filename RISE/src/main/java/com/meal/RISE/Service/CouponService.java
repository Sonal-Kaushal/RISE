package com.meal.RISE.Service;

import com.meal.RISE.Entity.Coupon;
import com.meal.RISE.Entity.Booking;
import com.meal.RISE.Enums.Status;
import com.meal.RISE.Repository.CouponRepository;
import com.meal.RISE.Repository.BookingRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class CouponService {

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Transactional

    public Coupon generateCoupon(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        // Check if today's date matches the booking start date
        LocalDate today = LocalDate.now();
        if (!today.equals(booking.getStartDate())) {
            throw new RuntimeException("Coupon can only be generated on the booking date");
        }

        // Check if a coupon has already been generated for today's booking
        Coupon existingCoupon = booking.getCoupon();
        if (existingCoupon != null && existingCoupon.getExpirationTime().toLocalDate().isEqual(today)) {
            throw new RuntimeException("Coupon has already been generated for today's booking");
        }

        // Create a new coupon
        Coupon newCoupon = new Coupon();
        newCoupon.setCouponId(generateUniqueCouponId());
        newCoupon.setExpirationTime(LocalDateTime.now().plusSeconds(1)); // Set expiration time to 1 second
        newCoupon.setStatus(Status.CREATED); // Set status to created
        newCoupon.setBooking(booking);
        booking.setCoupon(newCoupon);

        return couponRepository.save(newCoupon);
    }


    public void redeemCoupon(String couponId) {
        Coupon coupon = couponRepository.findByCouponId(couponId);
        if (coupon != null && coupon.getExpirationTime().isAfter(LocalDateTime.now())) {
            coupon.setStatus(Status.REDEEMED);
            couponRepository.save(coupon);
        } else {
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

    @Scheduled(fixedRate = 60000)
    public void deleteExpiredCoupons() {
        LocalDateTime now = LocalDateTime.now();
        List<Coupon> expiredCoupons = couponRepository.findByExpirationTimeBefore(now);
        for (Coupon coupon : expiredCoupons) {
            couponRepository.delete(coupon);
        }
    }

    public ResponseEntity<String> validateCoupon(String couponId) {
        Coupon coupon = couponRepository.findByCouponId(couponId);
        if (coupon != null) {
            if (coupon.getExpirationTime().isBefore(LocalDateTime.now())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Coupon is expired");
            } else {
                return ResponseEntity.ok("Coupon is valid");
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Coupon not found");
        }
    }
}
