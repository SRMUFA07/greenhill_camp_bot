package com.example.campbot.dto;

import jakarta.validation.constraints.Pattern;

public class ChildFullNameDto {
    @Pattern(regexp = "^[А-Яа-яЁё]+\\s+[А-Яа-яЁё]+\\s+[А-Яа-яЁё]+$",
            message = "Неверный формат ФИО. Используйте формат: Иванов Иван Иванович")
    private String fullName;

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
}