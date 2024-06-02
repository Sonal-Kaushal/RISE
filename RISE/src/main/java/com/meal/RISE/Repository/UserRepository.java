package com.meal.RISE.Repository;

import com.meal.RISE.Entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<Employee,Long> {
    Employee findByEmail(String email);

    Optional<Employee> findById(Long id);

}
