# Library Management System

Java + MySQL + DSA console application for managing books, students, issues, returns, fines, and reservations.

## Build

```powershell
.\build.ps1
```

## Run

```powershell
.\run.ps1
```

## Database

The application reads these environment variables:

- `LMS_DB_URL`
- `LMS_DB_USER`
- `LMS_DB_PASSWORD`

Default connection string:

```text
jdbc:mysql://localhost:3306/LibraryManagementDB?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
```

Default user: `root`

Default password: empty

## Notes

- The project keeps the existing `src` layout instead of Maven's default `src/main/java`.
- If the MySQL driver is missing, the build script downloads it automatically into `lib`.
- `build.bat` and `run.bat` are provided for double-click or `cmd.exe` usage on Windows.