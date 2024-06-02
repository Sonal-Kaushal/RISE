package com.meal.RISE.Entity;

import com.meal.RISE.Enums.MealType;
import com.meal.RISE.Enums.Status;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Data
@Getter
@Setter
@Builder
@Table(name = "booking")
@AllArgsConstructor
//@NoArgsConstructor
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Employee employee;

    private LocalDate startDate;

    private LocalDate endDate;
//
//    @Enumerated(EnumType.STRING)
    private MealType mealType;


    @Enumerated(EnumType.STRING)
    private Status status;

    @OneToOne(mappedBy = "booking", cascade = CascadeType.ALL)
    private Coupon coupon;

    public Coupon getCoupon() {
        return coupon;
    }

    public void setCoupon(Coupon coupon) {
        this.coupon = coupon;
    }

    public Booking() {

    }

    public Booking(Employee employee, LocalDate endDate, Long id, MealType mealType, LocalDate startDate, Status status) {
        this.employee = employee;
        this.endDate = endDate;
        this.id = id;
        this.mealType = mealType;
        this.startDate = startDate;
        this.status = status;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public MealType getMealType() {
        return mealType;
    }

    public void setMealType(MealType mealType) {
        this.mealType = mealType;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
