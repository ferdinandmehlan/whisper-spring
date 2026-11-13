plugins {
    id("java-library")
    id("maven-publish")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(libs.versions.java.get()))
    }

    withSourcesJar()
    withJavadocJar()
}

dependencies {
    api(platform(libs.springBootDependencies))
    api(platform(libs.springAiBom))
    api(libs.springBootStarter)
    api(libs.springAiModel)

    testImplementation(project(":test-common"))

    testRuntimeOnly(libs.junitPlatformLauncher)
}

tasks.jar {
    archiveBaseName.set("whisper-spring")
}

/*
 * Check
 */

tasks.processTestResources {
    from(project(":test-common").layout.buildDirectory.dir("resources/main"))
    dependsOn(":test-common:processResources", ":downloadTinyModel")
}

tasks.test {
    useJUnitPlatform()
    jvmArgs("--enable-native-access=ALL-UNNAMED")
}

tasks.javadoc {
    options {
        this as StandardJavadocDocletOptions
        addStringOption("Xdoclint:all,-missing", "-quiet")
    }
}
