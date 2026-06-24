from __future__ import annotations

import csv
from dataclasses import dataclass
from decimal import Decimal, InvalidOperation
from pathlib import Path


PROJECT_ROOT = Path(__file__).resolve().parents[1]

RAW_UNIVERSITIES_PATH = PROJECT_ROOT / "data" / "raw" / "universities.csv"
RAW_PROGRAMS_PATH = PROJECT_ROOT / "data" / "raw" / "programs.csv"

PROCESSED_DIR = PROJECT_ROOT / "data" / "processed"
CLEAN_UNIVERSITIES_PATH = PROCESSED_DIR / "universities_clean.csv"
CLEAN_PROGRAMS_PATH = PROCESSED_DIR / "programs_clean.csv"
IMPORT_REPORT_PATH = PROCESSED_DIR / "import_report.txt"


UNIVERSITY_COLUMNS = [
    "name",
    "country",
    "state",
    "city",
    "min_gpa",
    "application_deadline",
    "website_url",
    "accepts_international",
    "accepts_fall",
    "accepts_spring",
    "application_portal",
    "transfer_requirements",
]

PROGRAM_COLUMNS = [
    "university_name",
    "major_name",
    "degree_level",
    "estimated_annual_cost",
    "minimum_recommended_gpa",
    "competitiveness_level",
    "program_url",
]


@dataclass(frozen=True)
class UniversityRow:
    name: str
    country: str
    state: str
    city: str
    min_gpa: Decimal
    application_deadline: str
    website_url: str
    accepts_international: bool
    accepts_fall: bool
    accepts_spring: bool
    application_portal: str
    transfer_requirements: str


@dataclass(frozen=True)
class ProgramRow:
    university_name: str
    major_name: str
    degree_level: str
    estimated_annual_cost: Decimal
    minimum_recommended_gpa: Decimal
    competitiveness_level: str
    program_url: str


class DatasetValidationError(Exception):
    pass


def main() -> None:
    PROCESSED_DIR.mkdir(parents=True, exist_ok=True)

    universities = load_universities(RAW_UNIVERSITIES_PATH)
    programs = load_programs(RAW_PROGRAMS_PATH, universities)

    write_clean_universities(universities, CLEAN_UNIVERSITIES_PATH)
    write_clean_programs(programs, CLEAN_PROGRAMS_PATH)
    write_import_report(universities, programs, IMPORT_REPORT_PATH)

    print("Import pipeline completed successfully.")
    print(f"Clean universities: {CLEAN_UNIVERSITIES_PATH}")
    print(f"Clean programs: {CLEAN_PROGRAMS_PATH}")
    print(f"Import report: {IMPORT_REPORT_PATH}")


def load_universities(path: Path) -> list[UniversityRow]:
    rows = read_csv(path, UNIVERSITY_COLUMNS)

    universities: list[UniversityRow] = []
    seen_names: set[str] = set()

    for line_number, row in rows:
        name = normalize_text(row["name"])

        if name.lower() in seen_names:
            raise DatasetValidationError(f"Duplicate university name on line {line_number}: {name}")

        seen_names.add(name.lower())

        universities.append(
            UniversityRow(
                name=name,
                country=normalize_text(row["country"]),
                state=normalize_text(row["state"]),
                city=normalize_text(row["city"]),
                min_gpa=parse_decimal(row["min_gpa"], "min_gpa", line_number),
                application_deadline=normalize_text(row["application_deadline"]),
                website_url=normalize_url(row["website_url"], line_number),
                accepts_international=parse_bool(row["accepts_international"], "accepts_international", line_number),
                accepts_fall=parse_bool(row["accepts_fall"], "accepts_fall", line_number),
                accepts_spring=parse_bool(row["accepts_spring"], "accepts_spring", line_number),
                application_portal=normalize_text(row["application_portal"]),
                transfer_requirements=normalize_text(row["transfer_requirements"]),
            )
        )

    return universities


def load_programs(path: Path, universities: list[UniversityRow]) -> list[ProgramRow]:
    rows = read_csv(path, PROGRAM_COLUMNS)

    valid_university_names = {university.name.lower() for university in universities}
    programs: list[ProgramRow] = []
    seen_program_keys: set[tuple[str, str, str]] = set()

    for line_number, row in rows:
        university_name = normalize_text(row["university_name"])
        major_name = normalize_text(row["major_name"])
        degree_level = normalize_text(row["degree_level"])

        if university_name.lower() not in valid_university_names:
            raise DatasetValidationError(
                f"Program line {line_number} references unknown university: {university_name}"
            )

        program_key = (
            university_name.lower(),
            major_name.lower(),
            degree_level.lower(),
        )

        if program_key in seen_program_keys:
            raise DatasetValidationError(
                f"Duplicate program on line {line_number}: {university_name} / {major_name} / {degree_level}"
            )

        seen_program_keys.add(program_key)

        programs.append(
            ProgramRow(
                university_name=university_name,
                major_name=major_name,
                degree_level=degree_level,
                estimated_annual_cost=parse_decimal(
                    row["estimated_annual_cost"],
                    "estimated_annual_cost",
                    line_number,
                ),
                minimum_recommended_gpa=parse_decimal(
                    row["minimum_recommended_gpa"],
                    "minimum_recommended_gpa",
                    line_number,
                ),
                competitiveness_level=normalize_text(row["competitiveness_level"]),
                program_url=normalize_url(row["program_url"], line_number),
            )
        )

    return programs


def read_csv(path: Path, required_columns: list[str]) -> list[tuple[int, dict[str, str]]]:
    if not path.exists():
        raise DatasetValidationError(f"Missing file: {path}")

    with path.open("r", encoding="utf-8", newline="") as file:
        reader = csv.DictReader(file)

        if reader.fieldnames is None:
            raise DatasetValidationError(f"CSV file has no header: {path}")

        missing_columns = [column for column in required_columns if column not in reader.fieldnames]

        if missing_columns:
            raise DatasetValidationError(f"{path} is missing columns: {missing_columns}")

        rows: list[tuple[int, dict[str, str]]] = []

        for line_number, row in enumerate(reader, start=2):
            rows.append((line_number, row))

        return rows


def write_clean_universities(universities: list[UniversityRow], path: Path) -> None:
    with path.open("w", encoding="utf-8", newline="") as file:
        writer = csv.DictWriter(file, fieldnames=UNIVERSITY_COLUMNS)
        writer.writeheader()

        for university in universities:
            writer.writerow(
                {
                    "name": university.name,
                    "country": university.country,
                    "state": university.state,
                    "city": university.city,
                    "min_gpa": format_decimal(university.min_gpa),
                    "application_deadline": university.application_deadline,
                    "website_url": university.website_url,
                    "accepts_international": format_bool(university.accepts_international),
                    "accepts_fall": format_bool(university.accepts_fall),
                    "accepts_spring": format_bool(university.accepts_spring),
                    "application_portal": university.application_portal,
                    "transfer_requirements": university.transfer_requirements,
                }
            )


def write_clean_programs(programs: list[ProgramRow], path: Path) -> None:
    with path.open("w", encoding="utf-8", newline="") as file:
        writer = csv.DictWriter(file, fieldnames=PROGRAM_COLUMNS)
        writer.writeheader()

        for program in programs:
            writer.writerow(
                {
                    "university_name": program.university_name,
                    "major_name": program.major_name,
                    "degree_level": program.degree_level,
                    "estimated_annual_cost": format_decimal(program.estimated_annual_cost),
                    "minimum_recommended_gpa": format_decimal(program.minimum_recommended_gpa),
                    "competitiveness_level": program.competitiveness_level,
                    "program_url": program.program_url,
                }
            )


def write_import_report(
    universities: list[UniversityRow],
    programs: list[ProgramRow],
    path: Path,
) -> None:
    program_count_by_university: dict[str, int] = {}

    for program in programs:
        program_count_by_university[program.university_name] = (
            program_count_by_university.get(program.university_name, 0) + 1
        )

    average_university_gpa = sum(university.min_gpa for university in universities) / len(universities)
    average_program_gpa = sum(program.minimum_recommended_gpa for program in programs) / len(programs)
    average_program_cost = sum(program.estimated_annual_cost for program in programs) / len(programs)

    lines = [
        "TransferPath Import Report",
        "==========================",
        "",
        f"Universities processed: {len(universities)}",
        f"Programs processed: {len(programs)}",
        f"Average university minimum GPA: {format_decimal(average_university_gpa)}",
        f"Average program recommended GPA: {format_decimal(average_program_gpa)}",
        f"Average estimated annual program cost: ${round(average_program_cost):,}",
        "",
        "Program count by university:",
    ]

    for university_name in sorted(program_count_by_university):
        lines.append(f"- {university_name}: {program_count_by_university[university_name]}")

    path.write_text("\n".join(lines) + "\n", encoding="utf-8")


def normalize_text(value: str | None) -> str:
    if value is None:
        return ""

    return " ".join(value.strip().split())


def normalize_url(value: str | None, line_number: int) -> str:
    normalized = normalize_text(value)

    if not normalized.startswith("https://"):
        raise DatasetValidationError(f"Expected https URL on line {line_number}: {normalized}")

    return normalized


def parse_decimal(value: str | None, column_name: str, line_number: int) -> Decimal:
    normalized = normalize_text(value)

    try:
        return Decimal(normalized)
    except InvalidOperation as error:
        raise DatasetValidationError(
            f"Invalid decimal for {column_name} on line {line_number}: {normalized}"
        ) from error


def parse_bool(value: str | None, column_name: str, line_number: int) -> bool:
    normalized = normalize_text(value).lower()

    if normalized == "true":
        return True

    if normalized == "false":
        return False

    raise DatasetValidationError(
        f"Invalid boolean for {column_name} on line {line_number}: {value}"
    )


def format_decimal(value: Decimal) -> str:
    return str(value.normalize())


def format_bool(value: bool) -> str:
    return "true" if value else "false"


if __name__ == "__main__":
    main()