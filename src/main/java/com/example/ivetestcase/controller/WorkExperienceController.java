package com.example.ivetestcase.controller;

import com.example.ivetestcase.dto.WorkExperienceDTO;
import com.example.ivetestcase.response.CommonResponse;
import com.example.ivetestcase.service.WorkExperienceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/personalDetails/{personalDetailsId}/work-experiences")
@RequiredArgsConstructor
@Validated
public class WorkExperienceController {

    private static final Logger logger = LoggerFactory.getLogger(WorkExperienceController.class);

    private final WorkExperienceService workExperienceService;

    @Operation(summary = "Add new work experience to a profile")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Work experience added"),
            @ApiResponse(responseCode = "404", description = "Profile not found"),
            @ApiResponse(responseCode = "400", description = "Validation error")
    })
    @PostMapping
    public ResponseEntity<CommonResponse<WorkExperienceDTO>> addExperience(
            @PathVariable Long personalDetailsId,
            @Valid @RequestBody WorkExperienceDTO dto) {

        logger.info("Adding work experience to profile ID: {}", personalDetailsId);
        WorkExperienceDTO saved = workExperienceService.addExperience(personalDetailsId, dto);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CommonResponse.success("Work experience added successfully", saved));
    }

    @Operation(summary = "Get all work experiences for a profile")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Experiences fetched"),
            @ApiResponse(responseCode = "404", description = "Profile not found")
    })
    @GetMapping
    public ResponseEntity<CommonResponse<List<WorkExperienceDTO>>> getAllExperiences(
            @PathVariable Long personalDetailsId) {

        logger.info("Fetching work experiences for profile ID: {}", personalDetailsId);
        List<WorkExperienceDTO> experiences = workExperienceService.getExperiencesByProfile(personalDetailsId);

        return ResponseEntity.ok(CommonResponse.success("Work experiences fetched successfully", experiences));
    }

    @Operation(summary = "Update an existing work experience")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Work experience updated"),
            @ApiResponse(responseCode = "404", description = "Work experience not found"),
            @ApiResponse(responseCode = "400", description = "Validation error")
    })
    @PutMapping("/{experienceId}")
    public ResponseEntity<CommonResponse<WorkExperienceDTO>> updateExperience(
            @PathVariable Long experienceId,
            @Valid @RequestBody WorkExperienceDTO dto) {

        logger.info("Updating work experience with ID: {}", experienceId);
        WorkExperienceDTO updated = workExperienceService.updateExperience(experienceId, dto);

        return ResponseEntity.ok(CommonResponse.success("Work experience updated successfully", updated));
    }

    @Operation(summary = "Delete a work experience")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Work experience deleted"),
            @ApiResponse(responseCode = "404", description = "Work experience not found")
    })
    @DeleteMapping("/{experienceId}")
    public ResponseEntity<CommonResponse<String>> deleteExperience(@PathVariable Long experienceId) {

        logger.info("Deleting work experience with ID: {}", experienceId);
        workExperienceService.deleteExperience(experienceId);

        return ResponseEntity.ok(CommonResponse.success("Work experience deleted successfully", null));
    }
}
