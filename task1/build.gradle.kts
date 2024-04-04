plugins {
    kotlin("jvm") version "1.9.22"
    kotlin("plugin.allopen") version "1.9.20"
    id("org.jetbrains.kotlinx.benchmark") version "0.4.10"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(20)
}

sourceSets {
    create("benchmark")
}

allOpen {
    annotation("org.openjdk.jmh.annotations.State")
}

benchmark {
    targets {
        register("benchmark")
    }
}

dependencies {
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    testImplementation("org.jetbrains.kotlinx:lincheck:2.26")
    implementation("org.jetbrains.kotlinx:kotlinx-benchmark-runtime:0.4.10")
    add("benchmarkImplementation", sourceSets.main.get().output + sourceSets.main.get().runtimeClasspath)
}



