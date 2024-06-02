package com.meal.RISE.Repository;



import com.meal.RISE.Entity.Booking;
import com.meal.RISE.Entity.Employee;
import com.meal.RISE.Enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
//    List<Booking> findByEmployeeIdAndStartDateBetween(Long employeeId, LocalDate startDate, LocalDate endDate);
//

    Optional<Object> findByEmployeeIdAndStartDateBetween(Long id, LocalDate startDate, LocalDate endDate);

    List<Booking> findByEmployeeIdAndStartDate(Long userId, LocalDate startDate);

    List<Booking> findByEmployee(Employee loggedInUser);

    List<Booking> findByStartDateIn(List<LocalDate> bookingDates);

    List<Booking> findByStartDate(LocalDate cancellationDate);

    List<Booking> findByEmployeeAndStatus(Employee user, Status status);

//    List<Booking> findByEmployeeAndStartDate(Employee employee, LocalDate date);
}

