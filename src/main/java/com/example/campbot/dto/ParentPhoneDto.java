package com.example.campbot.dto;

import jakarta.validation.constraints.Pattern;

public class ParentPhoneDto {
    @Pattern(regexp = "^(7|8|\\+7)\\d{10}$",
            message = "Неверный формат номера. Номер должен начинаться с +7, 7 или 8")
    private String phone;

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
}