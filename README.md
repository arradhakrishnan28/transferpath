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

## Ranking Model

TransferPath ranks universities using a weighted fit score from 0 to 100. The score is designed to explain not just which schools match a student, but why they match.

Each result includes a `fitScore`, a detailed `scoreBreakdown`, and human-readable `fitReasons`.

| Component | Max Points | What It Measures |
| --- | ---: | --- |
| University GPA | 18 | How the student's GPA compares with the university's minimum transfer GPA |
| Program GPA | 22 | How the student's GPA compares with the matched major/program GPA recommendation |
| Major Match | 20 | Whether the requested major matches an available program |
| International Support | 10 | Whether the university accepts international transfer students |
| Admission Term | 10 | Whether the university supports the requested fall or spring transfer term |
| Cost | 10 | How affordable the matched program is relative to other options |
| Competitiveness | 10 | How selective or competitive the matched program is |

The final score is computed as:

```text
fitScore =
  universityGpaScore
  + programGpaScore
  + majorMatchScore
  + internationalScore
  + termMatchScore
  + costScore
  + competitivenessScore

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
```

## Running the Data Pipeline

From the project root:

```bash
python3 scripts/import_universities.py
```

This validates the raw CSV files and generates clean processed files:

```text
data/processed/universities_clean.csv
data/processed/programs_clean.csv
data/processed/import_report.txt
```

## Running the Application

Make sure PostgreSQL is running and your `application.properties` points to your local database.

Then run:

```bash
./mvnw spring-boot:run
```

Open:

```text
http://localhost:8080
```

Useful API endpoints:

```text
GET /universities
GET /programs
GET /universities/search/filter?country=USA&gpa=3.8&international=true&fall=true&major=Computer%20Science
```

## Testing

Run:

```bash
./mvnw test
```

## Data Notes

The current dataset is an MVP-scale curated dataset used to validate the ranking system, import pipeline, and user experience.

Future versions will expand the dataset with larger public sources, richer source attribution, transfer requirement parsing, school comparison features, and AI-assisted explanation support.

## Roadmap

- Expand the dataset with more universities and program-level transfer information
- Add school comparison views
- Add transfer requirement parsing
- Improve the fit score with more transparent weighting
- Add AI-assisted fit explanations
- Add deployment so TransferPath can be used publicly