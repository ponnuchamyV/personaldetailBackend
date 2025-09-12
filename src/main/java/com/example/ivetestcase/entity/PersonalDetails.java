package com.example.ivetestcase.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "personal_details")
public class PersonalDetails extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // renamed from 'Id' to 'id'

    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 30)
    @Column(nullable = false)
    private String firstName;

    @Size(max = 30)
    private String lastName; // renamed from 'LastName' to 'lastName'

    private LocalDate dateOfBirth;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @NotBlank(message = "Phone number is required")
    @Size(min = 10, max = 15)
    private String phoneNumber; // changed from Long to String for safety

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    @Column(nullable = false, unique = true)
    private String email;

    private String state;

    private Integer pinCode;

    @NotBlank(message = "Blood group is required")
    @Column(nullable = false)
    private String bloodGroup;

    @OneToMany(mappedBy = "personalDetails", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WorkExperience> workExperiences;
}
