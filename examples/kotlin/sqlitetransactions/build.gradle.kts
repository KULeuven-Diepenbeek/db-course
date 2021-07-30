plugins {
    kotlin("jvm") version "1.5.21"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.xerial:sqlite-jdbc:3.32.3.2")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}