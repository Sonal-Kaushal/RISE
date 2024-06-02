package com.meal.RISE.DTO;

import com.meal.RISE.Enums.MealType;

import java.time.LocalDate;

public class SingleBookingRequestDto {
    private Long userId;
    private LocalDate startDate;
    private LocalDate endDate;
private MealType mealType;

    public SingleBookingRequestDto(LocalDate endDate, MealType mealType, LocalDate startDate, Long userId) {
        this.endDate = endDate;
        this.mealType = mealType;
        this.startDate = startDate;
        this.userId = userId;
    }

    public MealType getMealType() {
        return mealType;
    }

    public void setMealType(MealType mealType) {
        this.mealType = mealType;
    }

    public Long getUserId() {
        return userId;
    }


    public void setUserId(Long userId) {
        this.userId = userId;
    }


    // Constructor
    public SingleBookingRequestDto() {
    }


    // Getters and setters
    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

}

