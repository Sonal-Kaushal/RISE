package com.meal.RISE.Repository;

import com.meal.RISE.Entity.Employee;
import com.meal.RISE.Entity.Otp;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OtpRepository extends JpaRepository<Otp,Long> {
    Otp findByOtp(String otp);

    Otp findByUser(Employee user);
}
