plugins {
    id("java")
}

dependencies {
    compileOnly(platform("org.springframework.security:spring-security-bom:6.4.4"))

    compileOnly("org.springframework.security:spring-security-core")
    compileOnly("org.springframework.security:spring-security-web")

    compileOnly("jakarta.servlet:jakarta.servlet-api:6.0.0")
    compileOnly("com.fasterxml.jackson.core:jackson-databind")

}