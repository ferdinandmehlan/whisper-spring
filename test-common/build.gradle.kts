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

/*
 * Test assets
 */

sourceSets.main { resources { exclude("native/**") } }

tasks.processResources {
    from(layout.projectDirectory.dir("src/main/resources/native")) { into("native") }
}

tasks.jar {
    exclude("native/**")
}
