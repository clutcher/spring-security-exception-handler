project.description =  "Spring Boot starter for spring-security-exception-handler library."

plugins {
    id("java-library")
}

dependencies {
    api(project(":spring-security-exception-handler"))
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-autoconfigure")

    implementation("jakarta.servlet:jakarta.servlet-api")
}