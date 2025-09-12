package com.example.ivetestcase.controller;

import com.example.ivetestcase.dto.PersonalDetailsDTO;
import com.example.ivetestcase.exception.DuplicateResourceException;
import com.example.ivetestcase.exception.ProfileNotFoundException;
import com.example.ivetestcase.exception.handler.GlobalExceptionHandler;
import com.example.ivetestcase.service.PersonalService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PersonalDetailsController.class)
@Import(GlobalExceptionHandler.class)
class PersonalDetailsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PersonalService personalService;

    private PersonalDetailsDTO sampleDTO;

    @BeforeEach
    void setUp() {
        sampleDTO = PersonalDetailsDTO.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .gender(null)
                .phoneNumber("9876543210")
                .email("john.doe@example.com")
                .state("NY")
                .pinCode(123456)
                .bloodGroup("O+")
                .build();
    }

    // ----------- Create -----------

    @Test
    void testCreateProfile_Success() throws Exception {
        Mockito.when(personalService.createPersonalDetails(any(PersonalDetailsDTO.class)))
                .thenReturn(sampleDTO);

        mockMvc.perform(post("/api/personalDetails")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.isError", is(false)))
                .andExpect(jsonPath("$.message", containsString("created")))
                .andExpect(jsonPath("$.data.email", is("john.doe@example.com")));
    }
//
//    @Test
//    void testCreateProfile_ValidationError() throws Exception {
//        PersonalDetailsDTO invalidDTO = PersonalDetailsDTO.builder()
//                .firstName("")          // invalid
//                .email("invalid-email") // invalid
//                .phoneNumber("")        // invalid
//                .bloodGroup("")         // invalid
//                .build();
//
//        mockMvc.perform(post("/api/personalDetails")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(invalidDTO)))
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.isError", is(true)))
//                .andExpect(jsonPath("$.message", is("Validation failed")))
//                .andExpect(jsonPath("$.data.firstName", is("First name is required")))
//                .andExpect(jsonPath("$.data.email", is("Email should be valid")))
//                .andExpect(jsonPath("$.data.phoneNumber", is("Phone number is required")))
//                .andExpect(jsonPath("$.data.bloodGroup", is("Blood group is required")));
//    }

    @Test
    void testCreateProfile_Duplicate() throws Exception {
        Mockito.when(personalService.createPersonalDetails(any(PersonalDetailsDTO.class)))
                .thenThrow(new DuplicateResourceException("Profile already exists"));

        mockMvc.perform(post("/api/personalDetails")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleDTO)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.isError", is(true)))
                .andExpect(jsonPath("$.message", is("Profile already exists")));
    }

    // ----------- Get by ID -----------

    @Test
    void testGetProfileById_Success() throws Exception {
        Mockito.when(personalService.getDetailsById(1L)).thenReturn(sampleDTO);

        mockMvc.perform(get("/api/personalDetails/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isError", is(false)))
                .andExpect(jsonPath("$.data.firstName", is("John")));
    }

    @Test
    void testGetProfileById_NotFound() throws Exception {
        Mockito.when(personalService.getDetailsById(99L))
                .thenThrow(new ProfileNotFoundException("Profile not found"));

        mockMvc.perform(get("/api/personalDetails/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.isError", is(true)))
                .andExpect(jsonPath("$.message", is("Profile not found")));
    }

    // ----------- Get All -----------

    @Test
    void testGetAllProfiles_Success() throws Exception {
        Map<String, Object> mockResult = new HashMap<>();
        mockResult.put("content", Collections.singletonList(sampleDTO));
        mockResult.put("totalElements", 1);

        Mockito.when(personalService.getAllProfiles(0, 5)).thenReturn(mockResult);

        mockMvc.perform(get("/api/personalDetails?page=0&size=5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isError", is(false)))
                .andExpect(jsonPath("$.data.content[0].email", is("john.doe@example.com")))
                .andExpect(jsonPath("$.data.totalElements", is(1)));
    }

    @Test
    void testGetAllProfiles_Empty() throws Exception {
        Map<String, Object> mockResult = new HashMap<>();
        mockResult.put("content", Collections.emptyList());
        mockResult.put("totalElements", 0);

        Mockito.when(personalService.getAllProfiles(0, 5)).thenReturn(mockResult);

        mockMvc.perform(get("/api/personalDetails?page=0&size=5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content", hasSize(0)))
                .andExpect(jsonPath("$.data.totalElements", is(0)));
    }

    // ----------- Update -----------

    @Test
    void testUpdateProfile_Success() throws Exception {
        sampleDTO.setFirstName("UpdatedName");
        Mockito.when(personalService.updateDetails(eq(1L), any(PersonalDetailsDTO.class)))
                .thenReturn(sampleDTO);

        mockMvc.perform(put("/api/personalDetails/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.firstName", is("UpdatedName")));
    }
//
//    @Test
//    void testUpdateProfile_ValidationError() throws Exception {
//        PersonalDetailsDTO invalidDTO = PersonalDetailsDTO.builder()
//                .firstName("")
//                .phoneNumber("")
//                .bloodGroup("")
//                .email("invalid-email")
//                .build();
//
//        mockMvc.perform(put("/api/personalDetails/1")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(invalidDTO)))
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.isError", is(true)))
//                .andExpect(jsonPath("$.data.firstName", is("First name is required")))
//                .andExpect(jsonPath("$.data.email", is("Email should be valid")))
//                .andExpect(jsonPath("$.data.phoneNumber", is("Phone number is required")))
//                .andExpect(jsonPath("$.data.bloodGroup", is("Blood group is required")));
//    }

    @Test
    void testUpdateProfile_NotFound() throws Exception {
        Mockito.when(personalService.updateDetails(eq(99L), any(PersonalDetailsDTO.class)))
                .thenThrow(new ProfileNotFoundException("Profile not found"));

        mockMvc.perform(put("/api/personalDetails/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleDTO)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.isError", is(true)))
                .andExpect(jsonPath("$.message", is("Profile not found")));
    }

    // ----------- Delete -----------

    @Test
    void testDeleteProfile_Success() throws Exception {
        Mockito.when(personalService.deleteProfile(1L)).thenReturn("Profile deleted successfully");

        mockMvc.perform(delete("/api/personalDetails/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isError", is(false)))
                .andExpect(jsonPath("$.message", is("Profile deleted successfully")));
    }

    @Test
    void testDeleteProfile_NotFound() throws Exception {
        Mockito.when(personalService.deleteProfile(99L))
                .thenThrow(new ProfileNotFoundException("Profile not found"));

        mockMvc.perform(delete("/api/personalDetails/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.isError", is(true)))
                .andExpect(jsonPath("$.message", is("Profile not found")));
    }
}
