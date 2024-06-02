package com.meal.RISE.Service.jwt;

import com.meal.RISE.Entity.Booking;
import com.meal.RISE.Entity.Employee;
import com.meal.RISE.Repository.EmployeeRepository;
import com.meal.RISE.Repository.UserRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final EmployeeRepository employeeRepository;

    public UserDetailsServiceImpl(EmployeeRepository userRepository) {
        this.employeeRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        //Write logic to get user from db
        Optional<Employee> optionalUser = employeeRepository.findFirstByEmail(email);

        if(optionalUser.isEmpty()) throw new UsernameNotFoundException("User Not Found",null);
        return new User(optionalUser.get().getEmail(),optionalUser.get().getPassword(),new ArrayList<>());

    }


}
