package com.example.campbot.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "applications")
public class ApplicationRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "child_name", nullable = false)
    private String childName;

    @Column(name = "birthday_date", nullable = false, length = 10)
    private String birthdayDate; // Храним как строку "09.08.2007"

    @Column(name = "parent_phone_number", nullable = false, length = 20)
    private String parentPhoneNumber;

    @Column(name = "user_chat_id", nullable = false)
    private Long userChatId;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // Конструкторы
    public ApplicationRequest() {}

    public ApplicationRequest(String childName, String birthdayDate, String parentPhoneNumber, Long userChatId) {
        this.childName = childName;
        this.birthdayDate = birthdayDate;
        this.parentPhoneNumber = parentPhoneNumber;
        this.userChatId = userChatId;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // Геттеры и сеттеры
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getChildName() { return childName; }
    public void setChildName(String childName) { this.childName = childName; }

    public String getBirthdayDate() { return birthdayDate; }
    public void setBirthdayDate(String birthdayDate) { this.birthdayDate = birthdayDate; }

    public String getParentPhoneNumber() { return parentPhoneNumber; }
    public void setParentPhoneNumber(String parentPhoneNumber) { this.parentPhoneNumber = parentPhoneNumber; }

    public Long getUserChatId() { return userChatId; }
    public void setUserChatId(Long userChatId) { this.userChatId = userChatId; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}