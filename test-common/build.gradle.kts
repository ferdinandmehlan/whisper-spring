plugins {
    id("java-library")
    alias(libs.plugins.download)
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(libs.versions.java.get()))
    }
}

dependencies {
    api(platform(libs.springBootDependencies))
    api(libs.springBootStarterTest)
    api(libs.springBootStarterWeb)
    api(libs.validationFileAssertions)
    api(libs.jacksonDatabind)
}
