package com.meal.RISE.DTO;

import com.meal.RISE.Enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
//@NoArgsConstructor
public class AuthenticationResponse {

    private String jwt;

    private UserRole userRole;

    private Long userId;

    private String name;
    public AuthenticationResponse(String jwt, Long userId, UserRole userRole) {
        this.jwt = jwt;
        this.userId = userId;
        this.userRole = userRole;
    }

    public AuthenticationResponse() {

    }

    public String getJwt() {
        return jwt;
    }

    public void setJwt(String jwt) {
        this.jwt = jwt;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public UserRole getUserRole() {
        return userRole;
    }

    public void setUserRole(UserRole userRole) {
        this.userRole = userRole;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
