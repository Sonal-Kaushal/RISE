package com.meal.RISE.DTO;



import com.meal.RISE.Enums.MealType;
import lombok.Data;

@Data
public class QuickBookingRequestDto {
private  Long userId;
   private MealType mealType;

    public MealType getMealType() {
        return mealType;
    }

    public void setMealType(MealType mealType) {
        this.mealType = mealType;
    }

    public QuickBookingRequestDto(MealType mealType, Long userId) {
        this.mealType = mealType;
        this.userId = userId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }


}

