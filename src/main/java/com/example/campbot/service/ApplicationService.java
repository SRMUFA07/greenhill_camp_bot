package com.example.campbot.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.campbot.dto.ApplicationRequestDto;
import com.example.campbot.entity.ApplicationRequest;
import com.example.campbot.repository.ApplicationRequestRepository;

@Service
@Transactional
public class ApplicationService {

    private final ApplicationRequestRepository repository;

    public ApplicationService(ApplicationRequestRepository repository) {
        this.repository = repository;
    }

    public void saveApplication(ApplicationRequestDto dto, Long chatId) {
        ApplicationRequest application = new ApplicationRequest();
        application.setChildName(dto.getChildName());
        application.setBirthdayDate(dto.getBirthdayDate()); // Просто сохраняем строку
        application.setParentPhoneNumber(dto.getParentPhoneNumber());
        application.setUserChatId(chatId);

        repository.save(application);
    }
}