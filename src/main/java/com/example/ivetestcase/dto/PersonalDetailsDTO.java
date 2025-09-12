package com.example.ivetestcase.dto;

import com.example.ivetestcase.entity.Gender;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PersonalDetailsDTO {

    private Long id;

    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 30)
    private String firstName;

    @Size(max = 30)
    private String lastName;

    private LocalDate dateOfBirth;

    private Gender gender;

    @NotBlank(message = "Phone number is required")
    @Size(min = 10, max = 15)
    private String phoneNumber;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;

    private String state;

    private Integer pinCode;

    @NotBlank(message = "Blood group is required")
    private String bloodGroup;

    private List<WorkExperienceDTO> workExperiences;
}
