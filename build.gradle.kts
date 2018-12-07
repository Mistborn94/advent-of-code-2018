import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.3.11"
}

dependencies {
    compile(kotlin("stdlib-jdk8"))
    testCompile(kotlin("test"))
    testCompile(kotlin("test-junit"))
}

repositories {
    mavenCentral()
}