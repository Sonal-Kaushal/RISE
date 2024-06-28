package com.meal.RISE.Controller;

import com.meal.RISE.Entity.Coupon;
import com.meal.RISE.Service.CouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin("**")
@RequestMapping("/coupons")
public class CouponController {

    @Autowired
    private CouponService couponService;

    @PostMapping("/generate")
    public ResponseEntity<?> generateCoupon(@RequestParam Long id) {
        return couponService.generateCoupon(id);
    }


    @PostMapping("/redeem")
    public ResponseEntity<?> redeemCoupon(@RequestParam String couponId) {
        try {
            couponService.redeemCoupon(couponId);
            return ResponseEntity.ok("Coupon redeemed successfully");
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to redeem coupon. " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }
}
