plugins {
    id("java")
    application
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "io.deeplay.wezzen.demo"
version = "1.0-SNAPSHOT"

application {
    mainClass.set("io.deeplay.wezzen.demo.runner.Runner")
}

repositories {
    mavenCentral()
}

dependencies {

    implementation("io.micrometer:micrometer-core:1.11.5")
    implementation("io.micrometer:micrometer-registry-prometheus:1.11.5")

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
}

tasks.named<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>("shadowJar") {
    archiveBaseName.set("MicrometerDemo")
    archiveVersion.set("1.0-SNAPSHOT")
    manifest {
        attributes["Main-Class"] = "io.deeplay.wezzen.demo.runner.Runner"
    }
}