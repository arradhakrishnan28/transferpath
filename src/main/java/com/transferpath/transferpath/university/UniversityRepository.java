package com.transferpath.transferpath.university;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface UniversityRepository extends JpaRepository<University, Long> {

    List<University> findByMinGpaLessThanEqual(Double gpa);

    List<University> findByCountry(String country);

    List<University> findByCountryAndMinGpaLessThanEqual(String country, Double gpa);

    List<University> findByAcceptsInternational(Boolean accepted);



}