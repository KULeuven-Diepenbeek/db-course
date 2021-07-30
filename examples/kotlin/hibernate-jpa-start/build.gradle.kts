import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.5.21"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions.jvmTarget = "13"

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.hibernate:hibernate-core:5.5.5.Final")
    implementation("org.xerial:sqlite-jdbc:3.32.3.2")
    implementation("com.zsoltfabok:sqlite-dialect:1.0")
    implementation("javax.persistence:javax.persistence-api:2.2")

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

