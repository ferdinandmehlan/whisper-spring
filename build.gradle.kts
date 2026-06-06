import de.undercouch.gradle.tasks.download.Download
import nl.littlerobots.vcu.plugin.resolver.VersionSelectors

plugins {
    alias(libs.plugins.spotless)
    alias(libs.plugins.download)
    alias(libs.plugins.versionCatalogUpdate)
}

tasks.wrapper {
    gradleVersion = libs.versions.gradle.get()
    distributionType = Wrapper.DistributionType.ALL
}

val projectVersion: String = libs.versions.project.get()

allprojects {
    group = "io.github.ferdinandmehlan"
    version = projectVersion

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
            targetExclude("build")
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
    onlyIf { !dest.exists() }
}

/*
 * Version Catalog Update
 */

versionCatalogUpdate {
    sortByKey.set(false)
    keep {
        keepUnusedVersions.set(true)
    }
    versionSelector(VersionSelectors.STABLE)
}
