package com.example.ivetestcase.service;

import com.example.ivetestcase.dto.WorkExperienceDTO;
import java.util.List;

public interface WorkExperienceService {
    WorkExperienceDTO addExperience(Long personalDetailsId, WorkExperienceDTO dto);
    List<WorkExperienceDTO> getExperiencesByProfile(Long personalDetailsId);
    WorkExperienceDTO updateExperience(Long id, WorkExperienceDTO dto);
    void deleteExperience(Long id);
}
