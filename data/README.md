# TransferPath Data Pipeline

TransferPath uses a small structured dataset to model universities, transfer requirements, program-level admissions signals, estimated costs, and major-specific competitiveness.

The data pipeline is intentionally separated into raw and processed layers so the project can grow from manually curated data into larger real-world datasets without changing the application code.

## Directory Structure

```text
data/
  raw/
    universities.csv
    programs.csv
  processed/
    universities_clean.csv
    programs_clean.csv
    import_report.txt