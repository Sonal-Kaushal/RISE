package com.meal.RISE.Controller;

import com.meal.RISE.DTO.ChangePasswordRequest;
import com.meal.RISE.Entity.Employee;
import com.meal.RISE.Entity.Otp;
import com.meal.RISE.Service.OtpService;
import com.meal.RISE.Service.UserService;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@CrossOrigin("**")
@RestController
@RequestMapping("/api")
public class ForgotPasswordController {

    @Autowired
    OtpService otpService;

    ChangePasswordRequest changePasswordRequest;

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    UserService userService;


@GetMapping("/employee/{id}")
public ResponseEntity<String> getEmployeeById(@PathVariable Long id) {
    Optional<Employee> employee = userService.getEmployeeById(id);
    return employee.map(value -> ResponseEntity.ok(value.getName())).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
}
    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, String>> sendOtp(@RequestParam String email) throws MessagingException {
        Map<String, String> response = new HashMap<>();
        Employee employee = userService.findByEmail("a@a.com");

        if (employee == null) {
            response.put("message", "User not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        Otp otp = otpService.createOtp(employee);
        //emailService.sendOtpEmail(employee.getEmail(), otp.getOtp());
        response.put("message", "OTP has been sent to your email");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<Map<String, String>> verifyOtp(@RequestParam String email, @RequestParam String otp) {

        Map<String, String> response = new HashMap<>();
        Employee user = userService.findByEmail(email);

        if (user == null) {
            response.put("message", "User not found");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        Otp otpEntity = otpService.findByOtp(otp);

        if (otpEntity == null || !otpEntity.getUser().equals(user)) {
            response.put("message", "Invalid OTP!");
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(response);
        }

        if(new Date().after(otpEntity.getExpiryDate())){
            response.put("message", "OTP has been expired!");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        response.put("message","OTP has been verified, Proceed to reset password!");
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, String>> resetPassword(@RequestParam String email, @RequestParam String otp, @RequestParam String newPassword) {

        Map<String, String> response = new HashMap<>();

        Employee user = userService.findByEmail(email);
        if (user == null) {
            response.put("message", "User not found!");
            return ResponseEntity.badRequest().body(response);
        }

        Otp otpEntity = otpService.findByOtp(otp);
        if (otpEntity == null || !otpEntity.getUser().equals(user)) {
            response.put("message", "Invalid OTP!");
            return ResponseEntity.badRequest().body(response);
        }

        if(new Date().after(otpEntity.getExpiryDate())){
            response.put("message", "Otp has been expired!");
            return ResponseEntity.badRequest().body(response);
        }

        userService.updatePassword(user, newPassword);
        response.put("message", "Password has been successfully reset!");
        return ResponseEntity.ok().body(response);
    }


    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest changePasswordRequest) {
        String email = changePasswordRequest.getEmail();
        String oldPassword = changePasswordRequest.getOldPassword();
        String newPassword = changePasswordRequest.getNewPassword();

        Employee user = userService.findByEmail(email);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "User not found"));
        }

        if (!bCryptPasswordEncoder.matches(oldPassword, user.getPassword())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Old password is incorrect"));
        }

        userService.updatePassword(user, newPassword);
        return ResponseEntity.ok(Map.of("message", "Password has been successfully changed"));
    }
}

