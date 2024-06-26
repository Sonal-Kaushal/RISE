package com.meal.RISE.Service;


import com.meal.RISE.Entity.Employee;
import com.meal.RISE.Entity.Otp;
import com.meal.RISE.Repository.OtpRepository;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.Random;

@Service
public class OtpService {

    @Autowired
    OtpRepository otpRepository;

    @Autowired
    EmailService emailService;

    public Otp createOtp(Employee employee) throws MessagingException {

        Otp otp = otpRepository.findByUser(employee);

        if(otp == null){
            otp = new Otp();
            otp.setUser(employee);
        }


        String generatedOtp = generateOtp();
        otp.setOtp(generatedOtp);
        otp.setExpiryDate(calculateExpiryDate(5));
        Otp savedOtp = otpRepository.save(otp);

        // Pass the employee name along with the OTP
        emailService.sendOtpEmail(employee.getEmail(), employee.getName(), generatedOtp);

        return savedOtp;

    }

    public Otp findByOtp(String otp) {
        return otpRepository.findByOtp(otp);
    }

    private Date calculateExpiryDate(int expiryTimeInMinutes) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.MINUTE, expiryTimeInMinutes);
        return new Date(calendar.getTime().getTime());
    }

    private String generateOtp() {

        Random random = new Random();
        int otp = 1000 + random.nextInt(9000);
        return String.valueOf(otp);
    }
}
