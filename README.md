# TransferPath

TransferPath is a Spring Boot and PostgreSQL application for exploring transfer-friendly universities and comparing major-specific program fit.

The project combines a Java backend, PostgreSQL persistence, Flyway database migrations, a Python data pipeline, and a browser-based search interface.

## Current Features

- Search universities by country, GPA, international eligibility, admission term, and intended major
- Rank schools using a fit score based on:
    - university minimum GPA
    - program recommended GPA
    - major match
    - international transfer support
    - fall/spring transfer availability
    - program competitiveness
- View major-specific data:
    - matched major
    - program recommended GPA
    - estimated annual cost
    - competitiveness level
    - program URL
- Load university and program data from processed CSV files
- Validate and clean raw CSV data using a Python pipeline
- Manage database schema through Flyway migrations

## Tech Stack

- Java 21
- Spring Boot
- Spring Web
- Spring Data JPA
- PostgreSQL
- Flyway
- Thymeleaf
- Python 3
- HTML, CSS, JavaScript

## Project Structure

```text
src/main/java/com/transferpath/transferpath/
  DataLoader.java
  HomeController.java
  TransferpathApplication.java
  university/
    Program.java
    ProgramController.java
    ProgramRepository.java
    University.java
    UniversityController.java
    UniversityRepository.java
    UniversitySearchResult.java
    UniversityService.java

src/main/resources/
  db/migration/
    V1__create_university_table.sql
    V2__create_program_table.sql
  templates/
    index.html

data/
  raw/
    universities.csv
    programs.csv
  processed/
    universities_clean.csv
    programs_clean.csv
    import_report.txt

scripts/
  import_universities.py