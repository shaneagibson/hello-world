# Hello World — Claude Code Guide

## Project overview

Spring Boot 4 REST API written in Kotlin. Single endpoint (`GET /hello/world`) serving a JSON greeting. The OpenAPI spec is a hand-authored static YAML file; there is no annotation-driven spec generation.

## Tech stack

| Concern | Choice |
|---|---|
| Language | Kotlin 2.3.10 |
| Runtime | Java 25 (compilation toolchain), Java 21 (Gradle daemon) |
| Framework | Spring Boot 4.0.3 (Spring MVC, Actuator) |
| Build | Gradle 9.1.0 with configuration cache enabled |
| Tests | JUnit 5, MockMvc, REST Assured 6.0.0, Schemathesis |
| Load tests | Gatling 3.14.9.8 |

## Common commands

```bash
# Build and test (includes JaCoCo coverage verification)
./gradlew build

# Run tests only
./gradlew test

# Lint
./gradlew ktlintCheck
./gradlew ktlintFormat      # auto-fix formatting

# Static analysis
./gradlew spotbugsMain spotbugsTest

# Coverage report (open build/reports/jacoco/test/html/index.html)
./gradlew jacocoTestReport

# Dependency vulnerability scan
./gradlew dependencyCheckAnalyze

# SBOM generation
./gradlew cyclonedxBom

# Load tests
./gradlew gatlingRun

# Run the application
./gradlew bootRun
# or after building:
JAVA_HOME=$(/usr/libexec/java_home -v 25) java -jar build/libs/hello-world-0.0.1-SNAPSHOT.jar
```

## Project structure

```
src/
├── main/kotlin/com/example/helloworld/
│   ├── HelloWorldApplication.kt       # Spring Boot entry point
│   ├── controller/
│   │   ├── HelloController.kt         # GET /hello/world
│   │   └── OpenApiController.kt       # GET /api-docs (serves openapi.yaml)
│   └── model/
│       └── HelloResponse.kt           # Response data class
├── main/resources/
│   ├── application.properties
│   └── openapi.yaml                   # Hand-authored OpenAPI 3.0.3 spec
├── test/kotlin/com/example/helloworld/
│   ├── HelloWorldApplicationTest.kt   # Context load test
│   ├── controller/
│   │   ├── HelloControllerTest.kt     # @WebMvcTest slice tests
│   │   └── OpenApiControllerTest.kt   # @WebMvcTest slice tests
│   └── contract/
│       └── ApiContractTest.kt         # Full integration + REST Assured contract tests
└── gatling/kotlin/com/example/helloworld/simulations/
    ├── HelloWorldLoadTest.kt
    └── HelloWorldStressTest.kt
```

## Key conventions

### OpenAPI spec
The spec lives at `src/main/resources/openapi.yaml` and is served at `GET /api-docs` by `OpenApiController`. **Do not use `io.swagger.v3.oas.annotations` — the springdoc dependency has been removed.** When adding or changing endpoints, update `openapi.yaml` manually.

### Coverage
JaCoCo is configured to require **100% instruction, line, and branch coverage**. `HelloWorldApplication` is excluded. Every new class needs corresponding tests. Avoid Kotlin constructs that generate unreachable JaCoCo branches — use `contentAsByteArray` (annotated `@NonNull` in Spring, treated as non-null under `-Xjsr305=strict`) rather than `inputStream` when reading resources.

### SpotBugs rules that have come up
- **`CT_CONSTRUCTOR_THROW`**: The Kotlin Spring plugin makes `@RestController` classes `open`. Don't throw (or do work that can throw) in constructors — use `@PostConstruct` or `by lazy`.
- **`NP_NONNULL_RETURN_VIOLATION`**: `lateinit var` fields are null in bytecode until initialised. Return non-null types via `by lazy` backed by a `@NonNull`-annotated Spring API, not via `lateinit var`.
- **`ST_WRITE_TO_STATIC_FROM_INSTANCE_METHOD`**: Don't write to `RestAssured.baseURI` / `RestAssured.port` static fields. Use `RequestSpecBuilder` instead.

### Dual JDK setup
The project targets Java 25 bytecode but runs the Gradle daemon on Java 21 for tooling compatibility. In CI this is handled by two `actions/setup-java` steps (the JDK 25 install path is saved as a Gradle toolchain installation before switching to JDK 21 as `JAVA_HOME`). Locally, ensure `JAVA_HOME` points to Java 21 for Gradle and that a Java 25 JDK is discoverable as a toolchain.

### Pre-commit hook
Install with `./install-hooks.sh`. The hook runs in order: gitleaks → ktlint → SpotBugs → JaCoCo coverage verification. All must pass before a commit is accepted.

## CI pipeline

`.github/workflows/ci.yml` runs on push/PR to `main` and `develop`:

1. Gitleaks secret scan
2. ktlint
3. SpotBugs SAST (SARIF uploaded to GitHub Security)
4. Semgrep SAST (`p/security-audit`, `p/kotlin`, `p/owasp-top-ten`)
5. OWASP Dependency-Check (requires `NVD_API_KEY` secret)
6. License report
7. Gradle build + tests
8. Application start → Schemathesis contract tests → application stop
9. Artifact upload (JAR, test results)

Note: OWASP Dependency-Check is not compatible with Gradle's configuration cache — Gradle automatically bypasses the cache when that task runs.
