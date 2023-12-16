import org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
import org.gradle.api.tasks.testing.logging.TestLogEvent.*
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.9.20"
}

group = "dev.renette"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))

    testImplementation(kotlin("test-junit5"))
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.6.3")
}

tasks.withType<Test> {
    useJUnitPlatform()
    dependsOn("cleanTest")
    testLogging {
        events(FAILED, STANDARD_ERROR, SKIPPED, PASSED)
        exceptionFormat = FULL
        showStandardStreams = true
        showExceptions = true
        showCauses = true
    }
    //Increase max heap size for day 24 brute force solution
    jvmArgs = listOf("-Xmx4g")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "21"
}