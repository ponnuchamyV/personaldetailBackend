package com.example.ivetestcase.service;

import com.example.ivetestcase.dto.PersonalDetailsDTO;

import java.util.Map;
public interface PersonalService {
    PersonalDetailsDTO createPersonalDetails(PersonalDetailsDTO personalDetailsDTO);

    PersonalDetailsDTO getDetailsById(Long id);

    Map<String, Object> getAllProfiles(int page, int size);

    PersonalDetailsDTO updateDetails(Long id, PersonalDetailsDTO personalDetailsDTO);

    String deleteProfile(Long id);
}
