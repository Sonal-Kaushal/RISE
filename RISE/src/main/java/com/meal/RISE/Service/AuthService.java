package com.meal.RISE.Service;

import com.meal.RISE.DTO.EmployeeDto;
import com.meal.RISE.DTO.SignupRequest;
import com.meal.RISE.Repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public interface AuthService {
    EmployeeDto createUser(SignupRequest signupRequest);
}
