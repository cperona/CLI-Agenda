plugins {
    id("java")
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:6.0.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.mockito:mockito-junit-jupiter:5.+")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    implementation("mysql:mysql-connector-java:8.0.33")

}

tasks.test {
    useJUnitPlatform()
}
/*
tasks.register("runAllTests") {
    description = "Run all tests in the project"
    group = "verification"
    dependsOn("test")
}

// Also ensure the test task description is clear
tasks.named("test") {
    description = "Run all unit tests"
    group = "verification"
}*/