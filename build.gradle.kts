plugins {
    id("com.diffplug.spotless") version "8.2.1"
}

tasks.wrapper {
    gradleVersion = "9.3.1"
    distributionType = Wrapper.DistributionType.ALL
}

val javaVersion: JavaLanguageVersion by extra(JavaLanguageVersion.of("25"))

allprojects {
    group = "io.github.ferdinandmehlan"
    version = "0.1.0"

    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}

subprojects {
    apply(plugin = "com.diffplug.spotless")

    spotless {
        java {
            target("src/**/*.java")
            targetExclude("**/test-models/")
            removeUnusedImports()
            palantirJavaFormat()
            formatAnnotations()
            toggleOffOn()
        }
    }
}

/*
 * Spotless
 */

spotless {
    kotlinGradle {
        target("**/*.gradle.kts")
        targetExclude("whisper.cpp/")
        ktlint()
        trimTrailingWhitespace()
        leadingTabsToSpaces()
    }
}

/*
 * Download tiny model for testing
 */

tasks.register<Exec>("downloadTinyModel") {
    commandLine("$rootDir/models/download-model.sh", "tiny")
    onlyIf { !file("models/ggml-tiny.bin").exists() }
}
