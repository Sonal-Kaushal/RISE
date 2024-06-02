package com.meal.RISE.Entity;

import com.meal.RISE.Enums.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
//@NoArgsConstructor
@Entity
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String couponId;
    private LocalDateTime expirationTime;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="booking_Id", referencedColumnName = "id")
    private Booking booking;


    private Status status ;


    public Booking getBooking() {
        return booking;
    }

    public void setBooking(Booking booking) {
        this.booking = booking;
    }

    public String getCouponId() {
        return couponId;
    }

    public void setCouponId(String couponId) {
        this.couponId = couponId;
    }

    public LocalDateTime getExpirationTime() {
        return expirationTime;
    }

    public void setExpirationTime(LocalDateTime expirationTime) {
        this.expirationTime = expirationTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Coupon(){}

    public Status getStatus() {
        return status;
    }
    @Enumerated(EnumType.STRING)
    public void setStatus(Status status) {
        this.status = status;
    }

    public Coupon(Booking booking, String couponId, LocalDateTime expirationTime, Long id, Status status) {
        this.booking = booking;
        this.couponId = couponId;
        this.expirationTime = expirationTime;
        this.id = id;
        this.status = status;
    }
}