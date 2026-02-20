# Hello World Spring Boot Application

A simple Spring Boot REST API that exposes a single endpoint returning a "Hello, World!" message.

## Technologies

### Core
- **Spring Boot 4.0.3** - Application framework
- **Kotlin 2.3.10** - Programming language
- **Gradle 9.1.0** - Build tool (Kotlin DSL)
- **Java 25** - Runtime environment
- **SpringDoc OpenAPI 3.0.1** - API documentation and Swagger UI

### Security & Quality
- **ktlint 1.8.0** - Code formatter and linter
- **SpotBugs 4.9.8** - JVM bytecode static analysis and SAST
- **FindSecBugs 1.14.0** - SpotBugs security plugin
- **Semgrep** - Multi-language SAST tool
- **Gitleaks** - Secret scanning tool
- **OWASP Dependency-Check 12.2.0** - Dependency vulnerability scanning
- **Gradle License Report 3.1.1** - License compliance scanning
- **JaCoCo 0.8.14** - Code coverage analysis

## Prerequisites

- JDK 25 (compilation) and JDK 21 (Gradle daemon)
- Gradle 9.1.0+ (or use the included Gradle wrapper)
- Gitleaks (optional, for secret scanning) - [Installation Guide](https://github.com/gitleaks/gitleaks#installing)

## Project Structure

```
hello-world/
├── .github/
│   └── workflows/
│       └── ci.yml                      # GitHub Actions CI pipeline
├── hooks/
│   └── pre-commit                      # Pre-commit hook for security and linting
├── src/
│   └── main/
│       ├── kotlin/
│       │   └── com/example/helloworld/
│       │       ├── HelloWorldApplication.kt     # Main application class
│       │       └── controller/
│       │           └── HelloController.kt       # REST controller
│       └── resources/
│           └── application.properties           # Application configuration
├── .gitleaks.toml                      # Gitleaks configuration
├── .gitleaksignore                     # Gitleaks ignore patterns
├── build.gradle.kts                    # Gradle build configuration
├── settings.gradle.kts                 # Gradle settings
├── install-hooks.sh                    # Script to install Git hooks
└── gradlew                             # Gradle wrapper script
```

## Building the Application

Build the application using the Gradle wrapper:

```bash
./gradlew build
```

This will:
- Compile the Kotlin source code
- Run tests
- Create an executable JAR file in `build/libs/`

## Running the Application

### Using Gradle

```bash
./gradlew bootRun
```

### Using the JAR file

```bash
java -jar build/libs/hello-world-0.0.1-SNAPSHOT.jar
```

The application will start on `http://localhost:8080`

## API Endpoint

### GET /hello/world

Returns a simple greeting message.

**Request:**
```bash
curl http://localhost:8080/hello/world
```

**Response:**
```json
{
  "message": "Hello, World!"
}
```

**Status Code:** `200 OK`

## Running Tests

Execute the test suite:

```bash
./gradlew test
```

## Code Formatting and Linting

This project uses **ktlint** for Kotlin code formatting and linting to maintain consistent code style.

### Installing Git Hooks

To automatically run security checks, SAST, and linting before each commit, install the pre-commit hook:

```bash
./install-hooks.sh
```

Once installed, the following checks will run automatically before every commit:
1. **Secret Scanning** (Gitleaks) - Detects hardcoded secrets
2. **Code Formatting** (ktlint) - Ensures consistent code style
3. **SAST** (SpotBugs + FindSecBugs) - Identifies security issues and bugs

If any check fails, the commit will be blocked until issues are resolved.

### Manual Linting

Check code formatting:

```bash
./gradlew ktlintCheck
```

Automatically fix formatting issues:

```bash
./gradlew ktlintFormat
```

### Linting in CI/CD

The GitHub Actions workflow automatically runs ktlint checks on all pull requests and pushes to ensure code quality standards are maintained.

## Secret Scanning

This project uses **Gitleaks** to detect and prevent secrets (API keys, passwords, tokens) from being committed to the repository.

### Installation

Install Gitleaks:

**macOS:**
```bash
brew install gitleaks
```

**Linux:**
```bash
# Download from GitHub releases
wget https://github.com/gitleaks/gitleaks/releases/download/v8.18.0/gitleaks_8.18.0_linux_x64.tar.gz
tar -xzf gitleaks_8.18.0_linux_x64.tar.gz
sudo mv gitleaks /usr/local/bin/
```

**Windows:**
```bash
# Using Scoop
scoop install gitleaks
```

For other installation methods, see the [official documentation](https://github.com/gitleaks/gitleaks#installing).

### Pre-commit Hook

Once you've installed the Git hooks with `./install-hooks.sh`, Gitleaks will automatically scan staged files before each commit. If secrets are detected, the commit will be blocked.

### Manual Scanning

Scan all files for secrets:

```bash
gitleaks detect --verbose
```

Scan only staged files (what would be committed):

```bash
gitleaks protect --staged --verbose
```

### Handling False Positives

If Gitleaks flags a false positive, you can:

1. **Add to `.gitleaksignore`** - Add the file path or pattern to exclude it globally
2. **Use inline comments** - Add `gitleaks:allow` comment to specific lines:
   ```kotlin
   val notASecret = "not-a-real-token" // gitleaks:allow
   ```
3. **Update `.gitleaks.toml`** - Configure custom rules and allowlists

### Secret Scanning in CI/CD

The GitHub Actions workflow automatically runs Gitleaks on all commits to prevent secrets from being pushed to the repository.

## SAST (Static Application Security Testing)

This project uses multiple SAST tools to identify security vulnerabilities and code quality issues:

### SpotBugs (JVM Bytecode Analysis)

**SpotBugs** analyses compiled JVM bytecode to detect bugs and security vulnerabilities. The **FindSecBugs** plugin extends it with security-specific detectors covering OWASP Top 10 and more.

#### Running SpotBugs

```bash
./gradlew spotbugsMain spotbugsTest
```

#### Viewing Reports

- **HTML Report**: `build/reports/spotbugs/main/spotbugs.html` (open in browser)
- **SARIF Report**: `build/reports/spotbugs/main/spotbugs.sarif` (for GitHub Code Scanning)

### Semgrep (Multi-Language SAST)

**Semgrep** runs in CI/CD to detect security vulnerabilities using:

- `p/security-audit` - General security rules
- `p/kotlin` - Kotlin-specific security patterns
- `p/owasp-top-ten` - OWASP Top 10 vulnerabilities

Semgrep runs automatically in the CI pipeline and will fail the build if high-severity issues are found.

### Pre-commit SAST Checks

Once you've installed the Git hooks with `./install-hooks.sh`, SpotBugs will automatically run before each commit to catch issues early.

### SAST in CI/CD

The GitHub Actions workflow runs both SpotBugs and Semgrep on every push and pull request:

1. **SpotBugs + FindSecBugs** - Analyses JVM bytecode and uploads SARIF reports to GitHub Security
2. **Semgrep** - Scans for security vulnerabilities across the codebase

Results are visible in the GitHub Security tab under Code Scanning Alerts.

## CI/CD

This project includes a GitHub Actions workflow (`.github/workflows/ci.yml`) that:
- Runs on push to `main` or `develop` branches
- Runs on pull requests targeting `main` or `develop` branches
- Performs secret scanning with Gitleaks
- Runs SAST with SpotBugs and Semgrep
- Runs ktlint code quality checks
- Builds the application
- Runs tests
- Uploads build artifacts and security reports

## Configuration

Application configuration can be modified in `src/main/resources/application.properties`:

```properties
spring.application.name=hello-world
server.port=8080
```

## License

This project is provided as-is for educational purposes.
