package com.example.campbot.dto;

public class ApplicationRequestDto {
    private String childName;
    private String birthdayDate; // Храним как строку "09.08.2007"
    private String parentPhoneNumber;
    private Long userChatId;

    // Конструкторы
    public ApplicationRequestDto() {}

    // Геттеры и сеттеры
    public String getChildName() { return childName; }
    public void setChildName(String childName) { this.childName = childName; }

    public String getBirthdayDate() { return birthdayDate; }
    public void setBirthdayDate(String birthdayDate) { this.birthdayDate = birthdayDate; }

    public String getParentPhoneNumber() { return parentPhoneNumber; }
    public void setParentPhoneNumber(String parentPhoneNumber) { this.parentPhoneNumber = parentPhoneNumber; }

    public Long getUserChatId() { return userChatId; }
    public void setUserChatId(Long userChatId) { this.userChatId = userChatId; }
}