import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "2.5.5"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    kotlin("jvm") version "1.5.31"
    kotlin("plugin.spring") version "1.5.31"
}

group = "co.copper"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa:2.5.5")
    implementation("org.springframework.boot:spring-boot-starter-web:2.5.5")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.13.0")
    implementation("org.flywaydb:flyway-core:8.0.0")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-jackson:2.9.0")
    developmentOnly("org.springframework.boot:spring-boot-devtools:2.5.5")
    runtimeOnly("org.postgresql:postgresql:42.2.24.jre7")
    testImplementation("org.springframework.boot:spring-boot-starter-test:2.5.5")
    testImplementation("org.mockito.kotlin:mockito-kotlin:4.0.0")
    testImplementation("com.squareup.okhttp3:mockwebserver:3.14.9")
    testImplementation("io.zonky.test:embedded-database-spring-test:2.1.1")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "11"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
