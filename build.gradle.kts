import de.undercouch.gradle.tasks.download.Download

plugins {
    id("com.diffplug.spotless") version "7.0.4"
    id("de.undercouch.download") version "5.6.0"
}

tasks.wrapper {
    gradleVersion = "9.2.0"
    distributionType = Wrapper.DistributionType.ALL
}

val javaVersion by extra(JavaLanguageVersion.of("25"))

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

tasks.register<Download>("downloadTinyModel") {
    src("https://huggingface.co/ggerganov/whisper.cpp/resolve/main/ggml-tiny.bin")
    dest(file("models/ggml-tiny.bin"))
}
