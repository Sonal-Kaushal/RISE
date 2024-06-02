package com.meal.RISE.Repository;

import com.meal.RISE.Entity.Booking;
import com.meal.RISE.Entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    Optional<Employee> findFirstByEmail(String email);



    Employee findByEmail(String loggedInUserEmail);
}
