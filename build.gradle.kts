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
    plugins.apply("com.diffplug.spotless")

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
    dest(file("build/models/ggml-tiny.bin"))
    onlyIf { !dest.exists() }
}

/*
 * Native whisper.cpp libraries
 *
 * Builds the whisper.cpp / ggml shared libraries from the submodule for the machine and JVM that run
 * the build. The libraries are not checked in: unit tests and :server:bootRun consume the output of
 * the buildNativeLibraries task instead of committed artifacts.
 *
 * Requires CMake and a C/C++ toolchain on the PATH:
 *   - Windows: Visual Studio with the "Desktop development with C++" workload (or Ninja + MSVC)
 *   - macOS:   Xcode command line tools
 *   - Linux:   GCC or Clang, make or ninja
 */

val nativeLibraryDirectory = layout.buildDirectory.dir("native/lib")
val nativeBuildDirectory = layout.buildDirectory.dir("native/build")
val whisperCppSources =
    fileTree("$rootDir/whisper.cpp") {
        exclude("build/**", "**/build/**", ".git/**")
    }
val nativeRpath = if (System.getProperty("os.name").lowercase().contains("mac")) "@loader_path" else "\$ORIGIN"

tasks.register<Exec>("configureNativeLibraries") {
    group = "build"
    description = "Configures the whisper.cpp build for the local platform"

    commandLine(
        "cmake",
        "-S",
        "$rootDir/whisper.cpp",
        "-B",
        nativeBuildDirectory.get().asFile.absolutePath,
        "-DCMAKE_BUILD_TYPE=Release",
        "-DBUILD_SHARED_LIBS=ON",
        "-DGGML_NATIVE=OFF",
        "-DGGML_OPENMP=ON",
        "-DWHISPER_BUILD_TESTS=OFF",
        "-DWHISPER_BUILD_EXAMPLES=OFF",
        "-DWHISPER_BUILD_SERVER=OFF",
        // Write the shared libraries into one flat directory, so nothing has to be collected from
        // the build tree. The _RELEASE variants keep multi-config generators such as MSVC flat.
        "-DCMAKE_LIBRARY_OUTPUT_DIRECTORY=${nativeLibraryDirectory.get().asFile}",
        "-DCMAKE_RUNTIME_OUTPUT_DIRECTORY=${nativeLibraryDirectory.get().asFile}",
        "-DCMAKE_LIBRARY_OUTPUT_DIRECTORY_RELEASE=${nativeLibraryDirectory.get().asFile}",
        "-DCMAKE_RUNTIME_OUTPUT_DIRECTORY_RELEASE=${nativeLibraryDirectory.get().asFile}",
        // Let the libraries resolve the ggml dependencies next to themselves.
        "-DCMAKE_INSTALL_RPATH=$nativeRpath",
        "-DCMAKE_BUILD_WITH_INSTALL_RPATH=ON",
    )

    inputs.files(whisperCppSources).withPathSensitivity(PathSensitivity.RELATIVE)
    outputs.file(nativeBuildDirectory.map { it.file("CMakeCache.txt") })
}

tasks.register<Exec>("buildNativeLibraries") {
    group = "build"
    description = "Builds the whisper.cpp native libraries for the local platform into build/native/lib"

    dependsOn("configureNativeLibraries")
    commandLine(
        "cmake",
        "--build",
        nativeBuildDirectory.get().asFile.absolutePath,
        "--config",
        "Release",
        "--parallel",
    )

    inputs.files(whisperCppSources).withPathSensitivity(PathSensitivity.RELATIVE)
    outputs.dir(nativeLibraryDirectory)
}

/*
 * Dependency Updates
 */

versionCatalogUpdate {
    sortByKey.set(false)
    keep {
        keepUnusedVersions.set(true)
    }
    versionSelector(VersionSelectors.STABLE)
}

tasks.register("updateDependencies") {
    description = "Updates all gradle and node dependencies"
    dependsOn(
        ":server-ui:updateDependencies",
        "versionCatalogUpdate",
    )
}
