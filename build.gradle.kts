import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.3.11"
}

dependencies {
    compile(kotlin("stdlib-jdk8"))
    testCompile(kotlin("test"))
    testCompile(kotlin("test-junit5"))
    testCompile("org.junit.jupiter:junit-jupiter-params:5.0.0")
}

repositories {
    mavenCentral()
}