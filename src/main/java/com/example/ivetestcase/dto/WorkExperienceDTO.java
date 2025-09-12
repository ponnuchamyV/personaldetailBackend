package com.example.ivetestcase.dto;

import lombok.*;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WorkExperienceDTO {
    private Long id;
    private String companyName;
    private String role;
    private LocalDate startDate;
    private LocalDate endDate;
}