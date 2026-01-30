project.description =
    "A library for handling Spring Security exceptions with customizable response formats, supporting both REST and GraphQL APIs."

dependencies {
    compileOnly(platform("org.springframework.security:spring-security-bom:7.0.2"))

    compileOnly("org.springframework.security:spring-security-core")
    compileOnly("org.springframework.security:spring-security-web")

    compileOnly("jakarta.servlet:jakarta.servlet-api:6.0.0")

    testImplementation("org.springframework.security:spring-security-core")
    testImplementation("org.springframework.security:spring-security-web")
    testImplementation("jakarta.servlet:jakarta.servlet-api:6.0.0")
    testImplementation("org.mockito:mockito-junit-jupiter:5.18.0")

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}