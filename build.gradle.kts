plugins {
    id("java")
}

group = "io.deeplay.wezzen.demo"
version = "1.0-SNAPSHOT"

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