package com.transferpath.transferpath.university;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UniversityRepository extends JpaRepository<University, Long> {

    List<University> findByMinGpaLessThanEqual(Double gpa);

    List<University> findByCountry(String country);

    List<University> findByCountryAndMinGpaLessThanEqual(String country, Double gpa);

    List<University> findByAcceptsInternational(Boolean accepted);

    List<University> findByAcceptsFall(Boolean accepted);

    List<University> findByAcceptsSpring(Boolean accepted);

    @Query("""
            SELECT u FROM University u
            WHERE (:country IS NULL OR :country = '' OR LOWER(u.country) = LOWER(:country))
            AND (:gpa IS NULL OR u.minGpa <= :gpa)
            AND (:international IS NULL OR u.acceptsInternational = :international)
            AND (:fall IS NULL OR u.acceptsFall = :fall)
            AND (:spring IS NULL OR u.acceptsSpring = :spring)
            """)
    List<University> filterUniversities(
            @Param("country") String country,
            @Param("gpa") Double gpa,
            @Param("international") Boolean international,
            @Param("fall") Boolean fall,
            @Param("spring") Boolean spring
    );
}