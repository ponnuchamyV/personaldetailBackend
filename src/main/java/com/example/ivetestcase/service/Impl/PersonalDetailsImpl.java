
package com.example.ivetestcase.service.Impl;

import com.example.ivetestcase.dto.PersonalDetailsDTO;
import com.example.ivetestcase.dto.WorkExperienceDTO;
import com.example.ivetestcase.entity.PersonalDetails;
import com.example.ivetestcase.entity.WorkExperience;
import com.example.ivetestcase.exception.DuplicateResourceException;
import com.example.ivetestcase.exception.ProfileNotFoundException;
import com.example.ivetestcase.repository.PersonalDetailsRepository;
import com.example.ivetestcase.repository.WorkExperienceRepository;
import com.example.ivetestcase.service.PersonalService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PersonalDetailsImpl implements PersonalService {

    private static final Logger logger = LoggerFactory.getLogger(PersonalDetailsImpl.class);

    private final PersonalDetailsRepository personalDetailsRepository;
    private final WorkExperienceRepository workExperienceRepository;
    private final ModelMapper modelMapper;

    private PersonalDetailsDTO toDTO(PersonalDetails personalDetails) {
        return modelMapper.map(personalDetails, PersonalDetailsDTO.class);
    }

    private WorkExperienceDTO toWorkDTO(WorkExperience workExperience) {
        return modelMapper.map(workExperience, WorkExperienceDTO.class);
    }

    private PersonalDetails toEntity(PersonalDetailsDTO personalDetailsDTO) {
        return modelMapper.map(personalDetailsDTO, PersonalDetails.class);
    }

    @Override
    public PersonalDetailsDTO createPersonalDetails(PersonalDetailsDTO personalDetailsDTO) {
        logger.info("Creating new personal profile for email: {}", personalDetailsDTO.getEmail());

        if (personalDetailsRepository.existsByEmail(personalDetailsDTO.getEmail())) {
            logger.warn("Duplicate email found: {}", personalDetailsDTO.getEmail());
            throw new DuplicateResourceException("Email already exists: " + personalDetailsDTO.getEmail());
        }

        if (personalDetailsRepository.existsByPhoneNumber(personalDetailsDTO.getPhoneNumber())) {
            logger.warn("Duplicate phone number found: {}", personalDetailsDTO.getPhoneNumber());
            throw new DuplicateResourceException("Phone number already exists: " + personalDetailsDTO.getPhoneNumber());
        }

        PersonalDetails personalDetails = personalDetailsRepository.save(toEntity(personalDetailsDTO));
        logger.info("Successfully created profile with ID: {}", personalDetails.getId());
        return toDTO(personalDetails);
    }

    @Override
    public PersonalDetailsDTO getDetailsById(Long id) {
        logger.info("Fetching personal profile with ID: {}", id);

        PersonalDetails personalDetails = personalDetailsRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Profile not found with ID: {}", id);
                    return new ProfileNotFoundException("Profile not found with ID " + id);
                });

        PersonalDetailsDTO dto = toDTO(personalDetails);

        // Attach work experiences
        List<WorkExperienceDTO> experiences = workExperienceRepository.findByPersonalDetailsId(id)
                .stream()
                .map(this::toWorkDTO)
                .collect(Collectors.toList());

        dto.setWorkExperiences(experiences);

        logger.info("Returning profile [{}] with {} work experiences", id, experiences.size());
        return dto;
    }

    @Override
    public Map<String, Object> getAllProfiles(int page, int size) {
        logger.info("Fetching all profiles with pagination: page={}, size={}", page, size);

        Pageable pageable = PageRequest.of(page, size);
        Page<PersonalDetails> personalDetailsPage = personalDetailsRepository.findAll(pageable);

        List<PersonalDetailsDTO> personalDetailsDTOS = personalDetailsPage.getContent()
                .stream()
                .map(this::toDTO) //  No work experiences here → keep lightweight
                .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("content", personalDetailsDTOS);
        response.put("page", personalDetailsPage.getNumber());
        response.put("size", personalDetailsPage.getSize());
        response.put("totalElements", personalDetailsPage.getTotalElements());
        response.put("totalPages", personalDetailsPage.getTotalPages());

        logger.info("Returning {} profiles in current page", personalDetailsDTOS.size());
        return response;
    }

    @Override
    public PersonalDetailsDTO updateDetails(Long id, PersonalDetailsDTO personalDetailsDTO) {
        logger.info("Updating profile with ID: {}", id);

        PersonalDetails existing = personalDetailsRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Profile not found with ID: {}", id);
                    return new ProfileNotFoundException("Profile not found with ID " + id);
                });

        modelMapper.map(personalDetailsDTO, existing); // updates fields

        PersonalDetails updated = personalDetailsRepository.save(existing);
        logger.info("Successfully updated profile with ID: {}", id);
        return toDTO(updated);
    }

    @Override
    public String deleteProfile(Long id) {
        logger.info("Deleting profile with ID: {}", id);

        PersonalDetails personalDetails = personalDetailsRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Profile not found with ID: {}", id);
                    return new ProfileNotFoundException("Profile not found with ID " + id);
                });

        personalDetailsRepository.delete(personalDetails);

        logger.info("Successfully deleted profile with ID: {}", id);
        return "Profile deleted successfully with ID " + id;
    }
}
