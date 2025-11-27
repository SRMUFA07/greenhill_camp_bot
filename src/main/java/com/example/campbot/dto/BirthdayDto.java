package com.example.campbot.dto;

import jakarta.validation.constraints.Pattern;

public class BirthdayDto {
    @Pattern(regexp = "^\\d{2}\\.\\d{2}\\.\\d{4}$", message = "Неверный формат даты. Используйте ДД.ММ.ГГГГ")
    private String birthday;

    public String getBirthday() { return birthday; }
    public void setBirthday(String birthday) { this.birthday = birthday; }
}