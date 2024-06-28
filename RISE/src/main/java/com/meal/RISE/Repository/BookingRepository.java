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
     boolean existsBystartDateAndStatus(LocalDate startDate, Status status);
    Optional<Object> findByEmployeeIdAndStartDateBetween(Long id, LocalDate startDate, LocalDate endDate);

    List<Booking> findByEmployeeIdAndStartDateAndStatus(Long userId, LocalDate startDate,Status status);

    List<Booking> findByEmployee(Employee loggedInUser);

    List<Booking> findByStartDateIn(List<LocalDate> bookingDates);

    List<Booking> findByStartDate(LocalDate cancellationDate);

    List<Booking> findByEmployeeAndStatus(Employee user, Status status);

    List<Booking> findByEmployeeIdAndStartDate(Long userId, LocalDate date);

//    Booking findByEmployeeId(Long userId);

    List<Booking> findByEmployeeId(Long employeeId);



//    List<Booking> findByEmployeeAndStartDate(Employee employee, LocalDate date);
}

