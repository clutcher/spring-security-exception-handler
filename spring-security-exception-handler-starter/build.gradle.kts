plugins {
    id("java")
    id("java-library")
}

dependencies {
    api(project(":spring-security-exception-handler"))
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-autoconfigure")

    implementation("jakarta.servlet:jakarta.servlet-api")
    implementation("com.fasterxml.jackson.core:jackson-databind")
}