package com.example.ivetestcase.repository;

import com.example.ivetestcase.entity.PersonalDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonalDetailsRepository extends JpaRepository<PersonalDetails,Long> {

    boolean existsByEmail(String email);
    boolean existsByPhoneNumber(String phoneNumber);

}
