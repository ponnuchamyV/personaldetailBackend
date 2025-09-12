package com.example.ivetestcase.service.Impl;

import com.example.ivetestcase.dto.WorkExperienceDTO;
import com.example.ivetestcase.entity.PersonalDetails;
import com.example.ivetestcase.entity.WorkExperience;
import com.example.ivetestcase.exception.ProfileNotFoundException;
import com.example.ivetestcase.repository.PersonalDetailsRepository;
import com.example.ivetestcase.repository.WorkExperienceRepository;
import com.example.ivetestcase.service.WorkExperienceService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WorkExperienceServiceImpl implements WorkExperienceService {

    private static final Logger logger = LoggerFactory.getLogger(WorkExperienceServiceImpl.class);

    private final WorkExperienceRepository workExperienceRepository;
    private final PersonalDetailsRepository personalDetailsRepository;
    private final ModelMapper modelMapper;

    private WorkExperienceDTO toDTO(WorkExperience entity) {
        return modelMapper.map(entity, WorkExperienceDTO.class);
    }

    private WorkExperience toEntity(WorkExperienceDTO dto) {
        return modelMapper.map(dto, WorkExperience.class);
    }

    @Override
    public WorkExperienceDTO addExperience(Long personalDetailsId, WorkExperienceDTO dto) {
        logger.info("Attempting to add new work experience for profile ID: {}", personalDetailsId);

        PersonalDetails personalDetails = personalDetailsRepository.findById(personalDetailsId)
                .orElseThrow(() -> {
                    logger.warn("Profile not found with ID: {}", personalDetailsId);
                    return new ProfileNotFoundException("Profile not found with ID: " + personalDetailsId);
                });

        WorkExperience experience = toEntity(dto);
        experience.setPersonalDetails(personalDetails);

        WorkExperience saved = workExperienceRepository.save(experience);

        logger.info("Successfully added work experience [{}] for profile ID: {}", saved.getId(), personalDetailsId);
        return toDTO(saved);
    }

    @Override
    public List<WorkExperienceDTO> getExperiencesByProfile(Long personalDetailsId) {
        logger.info("Fetching all work experiences for profile ID: {}", personalDetailsId);

        List<WorkExperience> experiences = workExperienceRepository.findByPersonalDetailsId(personalDetailsId);

        logger.debug("Found {} work experiences for profile ID: {}", experiences.size(), personalDetailsId);

        return experiences.stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public WorkExperienceDTO updateExperience(Long id, WorkExperienceDTO dto) {
        logger.info("Updating work experience with ID: {}", id);

        WorkExperience existing = workExperienceRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Work experience not found with ID: {}", id);
                    return new ProfileNotFoundException("Work experience not found with ID: " + id);
                });

        existing.setCompanyName(dto.getCompanyName());
        existing.setRole(dto.getRole());
        existing.setStartDate(dto.getStartDate());
        existing.setEndDate(dto.getEndDate());

        WorkExperience updated = workExperienceRepository.save(existing);

        logger.info("Successfully updated work experience [{}]", updated.getId());
        return toDTO(updated);
    }

    @Override
    public void deleteExperience(Long id) {
        logger.info("Deleting work experience with ID: {}", id);

        WorkExperience existing = workExperienceRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Work experience not found with ID: {}", id);
                    return new ProfileNotFoundException("Work experience not found with ID: " + id);
                });

        workExperienceRepository.delete(existing);

        logger.info("Successfully deleted work experience with ID: {}", id);
    }
}
