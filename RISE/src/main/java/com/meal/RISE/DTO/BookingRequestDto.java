package com.meal.RISE.DTO;

import com.meal.RISE.Enums.MealType;
import lombok.Data;

import java.time.LocalDate;

@Data
public class BookingRequestDto {
    private Long userId;
    private LocalDate startDate;
    private LocalDate endDate;
    private MealType mealType;

    public BookingRequestDto(LocalDate endDate, MealType mealType, LocalDate startDate, Long userId) {
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

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
