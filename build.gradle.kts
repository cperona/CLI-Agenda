plugins {
    id("java")
    id("application")
    id("com.gradleup.shadow") version "8.3.0"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.mysql:mysql-connector-j:8.4.0")

    testImplementation(platform("org.junit:junit-bom:6.0.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.mockito:mockito-junit-jupiter:5.+")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

application {
    mainClass.set("application.AgendaApp")
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = "application.AgendaApp"
    }
}

tasks.test {
    useJUnitPlatform()

}

tasks.register("runAllTests") {
    description = "Run all tests in the project"
    group = "verification"
    dependsOn("test")
}

tasks.named("test") {
    description = "Run all unit tests"
    group = "verification"
}