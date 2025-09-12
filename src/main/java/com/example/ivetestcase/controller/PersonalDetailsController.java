
package com.example.ivetestcase.controller;

import com.example.ivetestcase.dto.PersonalDetailsDTO;
import com.example.ivetestcase.response.CommonResponse;
import com.example.ivetestcase.service.PersonalService;
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

import java.util.Map;

@RestController
@RequestMapping("/api/personalDetails")
@RequiredArgsConstructor
@Validated
public class PersonalDetailsController {

    private static final Logger logger = LoggerFactory.getLogger(PersonalDetailsController.class);

    private final PersonalService personalService;

    @Operation(summary = "Create personal profile")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Profile created"),
            @ApiResponse(responseCode = "409", description = "Duplicate profile"),
            @ApiResponse(responseCode = "400", description = "Validation error")
    })
    @PostMapping
    public ResponseEntity<CommonResponse<PersonalDetailsDTO>> createPersonalDetails(
            @Valid @RequestBody PersonalDetailsDTO personalDetailsDTO) {

        logger.info("Creating personal details for: {}", personalDetailsDTO.getEmail());
        PersonalDetailsDTO savedDetails = personalService.createPersonalDetails(personalDetailsDTO);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CommonResponse.success("Personal details created successfully", savedDetails));
    }

    @Operation(summary = "Get personal profile by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profile found"),
            @ApiResponse(responseCode = "404", description = "Profile not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<CommonResponse<PersonalDetailsDTO>> getPersonalDetailsById(@PathVariable Long id) {

        logger.info("Fetching personal details for ID: {}", id);
        PersonalDetailsDTO details = personalService.getDetailsById(id);

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.success("Profile fetched successfully", details));
    }

    @Operation(summary = "Get all personal profiles (paginated)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profiles fetched")
    })
    @GetMapping
    public ResponseEntity<CommonResponse<Map<String, Object>>> getAllProfiles(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        logger.info("Fetching profiles - page: {}, size: {}", page, size);
        Map<String, Object> profiles = personalService.getAllProfiles(page, size);

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.success("Profiles fetched successfully", profiles));
    }

    @Operation(summary = "Update personal profile")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profile updated"),
            @ApiResponse(responseCode = "404", description = "Profile not found"),
            @ApiResponse(responseCode = "400", description = "Validation error")
    })
    @PutMapping("/{id}")
    public ResponseEntity<CommonResponse<PersonalDetailsDTO>> updateDetails(
            @PathVariable Long id,
            @Valid @RequestBody PersonalDetailsDTO personalDetailsDTO) {

        logger.info("Updating profile with ID: {}", id);
        PersonalDetailsDTO updated = personalService.updateDetails(id, personalDetailsDTO);

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.success("Profile updated successfully", updated));
    }

    @Operation(summary = "Delete personal profile")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Profile deleted"),
            @ApiResponse(responseCode = "404", description = "Profile not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<CommonResponse<String>> deleteProfile(@PathVariable Long id) {

        logger.info("Deleting profile with ID: {}", id);
        String message = personalService.deleteProfile(id);

        // Note: Returning 204 usually means no body, but you still return a CommonResponse with a message
        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.success(message, null));
    }
}
