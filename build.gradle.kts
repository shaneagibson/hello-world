import com.github.jk1.license.render.CsvReportRenderer
import com.github.jk1.license.render.InventoryHtmlReportRenderer
import com.github.jk1.license.render.JsonReportRenderer
import com.github.jk1.license.render.ReportRenderer
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.JvmTarget.Companion
import org.springframework.boot.gradle.plugin.SpringBootPlugin

buildscript {
    repositories { mavenCentral() }
    // plexus-utils 4.x moved XmlPullParserException to plexus-xml; add it so the
    // CycloneDX plugin's classloader (which delegates to this script classloader) can find it.
    dependencies {
        classpath("org.codehaus.plexus:plexus-xml:3.0.1")
    }
}

plugins {
    id("org.springframework.boot") version "4.0.3"
    kotlin("jvm") version "2.3.10"
    kotlin("plugin.spring") version "2.3.10"
    id("org.jlleitschuh.gradle.ktlint") version "14.0.1"
    id("com.github.spotbugs") version "6.4.8"
    id("org.owasp.dependencycheck") version "12.2.0"
    id("com.github.jk1.dependency-license-report") version "3.1.1"
    id("org.cyclonedx.bom") version "3.1.1"
    id("io.gatling.gradle") version "3.14.9.8"
    jacoco
}

group = "com.example"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(25))
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(platform(SpringBootPlugin.BOM_COORDINATES))
    implementation("org.springframework.boot:spring-boot-starter-webmvc")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("tools.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:3.0.1")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.boot:spring-boot-webmvc-test")
    testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")

    // API Contract Testing
    testImplementation("io.rest-assured:rest-assured:5.4.0")
    testImplementation("io.rest-assured:kotlin-extensions:5.4.0")
    testImplementation("com.atlassian.oai:swagger-request-validator-restassured:2.40.0")

    // SpotBugs security analysis
    spotbugsPlugins("com.h3xstream.findsecbugs:findsecbugs-plugin:1.14.0")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.add("-Xjsr305=strict")
        jvmTarget.set(JvmTarget.fromTarget("25"))
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

configure<org.jlleitschuh.gradle.ktlint.KtlintExtension> {
    version.set("1.8.0")
    android.set(false)
    outputToConsole.set(true)
    ignoreFailures.set(false)
}

spotbugs {
    toolVersion = "4.9.8"
    effort = com.github.spotbugs.snom.Effort.MAX
    reportLevel = com.github.spotbugs.snom.Confidence.MEDIUM
    ignoreFailures = false
}

tasks.spotbugsMain {
    reports.create("sarif") {
        required = true
        outputLocation = layout.buildDirectory.file("reports/spotbugs/main/spotbugs.sarif")
    }
    reports.create("html") {
        required = true
        outputLocation = layout.buildDirectory.file("reports/spotbugs/main/spotbugs.html")
    }
}

tasks.spotbugsTest {
    reports.create("sarif") {
        required = true
        outputLocation = layout.buildDirectory.file("reports/spotbugs/test/spotbugs.sarif")
    }
    reports.create("html") {
        required = true
        outputLocation = layout.buildDirectory.file("reports/spotbugs/test/spotbugs.html")
    }
}

jacoco {
    toolVersion = "0.8.14"
}

tasks.test {
    finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        xml.required.set(true)
        html.required.set(true)
        csv.required.set(false)
    }
    classDirectories.setFrom(
        files(
            classDirectories.files.map {
                fileTree(it) {
                    exclude("**/HelloWorldApplication*.class")
                }
            },
        ),
    )
}

tasks.jacocoTestCoverageVerification {
    dependsOn(tasks.jacocoTestReport)
    classDirectories.setFrom(
        files(
            classDirectories.files.map {
                fileTree(it) {
                    exclude("**/HelloWorldApplication*.class")
                }
            },
        ),
    )
    violationRules {
        rule {
            limit {
                minimum = "1.00".toBigDecimal()
            }
        }
        rule {
            limit {
                counter = "LINE"
                value = "COVEREDRATIO"
                minimum = "1.00".toBigDecimal()
            }
        }
        rule {
            limit {
                counter = "BRANCH"
                value = "COVEREDRATIO"
                minimum = "1.00".toBigDecimal()
            }
        }
    }
}

tasks.check {
    dependsOn(tasks.jacocoTestCoverageVerification)
}

dependencyCheck {
    formats = listOf("HTML", "JSON", "SARIF")
    failBuildOnCVSS = 7.0f
    suppressionFile = "$projectDir/dependency-check-suppressions.xml"
    analyzers.assemblyEnabled = false
    analyzers.nugetconfEnabled = false
    analyzers.nodeAudit.enabled = false
    nvd.apiKey = System.getenv("NVD_API_KEY") ?: ""
}

licenseReport {
    outputDir =
        layout.buildDirectory
            .dir("reports/licenses")
            .get()
            .asFile.path
    renderers =
        arrayOf<ReportRenderer>(
            JsonReportRenderer(),
            CsvReportRenderer(),
            InventoryHtmlReportRenderer(),
        )
    allowedLicensesFile = file("$projectDir/allowed-licenses.json")
}
