import com.github.gradle.node.pnpm.task.PnpmTask

plugins {
    id("com.github.node-gradle.node") version "7.1.0"
}

/*
 * Node
 */

node {
    version = "25.4.0"
    pnpmVersion = "10.28.1"
    download = true
    workDir = file("$rootDir/.gradle/nodejs")
    pnpmWorkDir = file("$rootDir/.gradle/pnpm")
}

fun Project.inputFiles() =
    listOf(
        fileTree("$projectDir/e2e") { include("**/*") },
        fileTree("$projectDir/src") { include("**/*") },
        file("$projectDir/eslint.config.js"),
        file("$projectDir/package.json"),
        file("$projectDir/playwright.config.ts"),
        file("$projectDir/pnpm-lock.yaml"),
        file("$projectDir/svelte.config.js"),
        file("$projectDir/tsconfig.json"),
        file("$projectDir/vite.config.ts"),
    )

/*
 * Clean
 */

tasks.register<Delete>("cleanSvelte") {
    group = "build"
    description = "Clean Svelte-specific build artifacts and dependencies"
    delete("$projectDir/node_modules")
    delete("$projectDir/.svelte-kit")
}

tasks.named("clean").configure {
    dependsOn("cleanSvelte")
}

/*
 * Svelte
 */

tasks.register<PnpmTask>("dev") {
    group = "build"
    description = "Start the development server"
    dependsOn("pnpmInstall")
    args = listOf("dev")
}

tasks.register<PnpmTask>("compile") {
    group = "build"
    description = "Build the Svelte application for production"
    dependsOn("pnpmInstall")
    inputs.files(inputFiles())
    outputs.dir("$projectDir/build") // Add this line
    outputs.dir("$projectDir/.svelte-kit/output") // Add this for incremental
    args = listOf("build")
}

tasks.register<PnpmTask>("svelte-check") {
    group = "verification"
    description = "Run Svelte type checking"
    dependsOn("pnpmInstall")
    inputs.files(inputFiles())
    outputs.upToDateWhen { true }
    args = listOf("check")
}

/*
 * Check
 */

tasks.register<PnpmTask>("lint") {
    group = "verification"
    description = "Run ESLint to check code quality"
    dependsOn("pnpmInstall")
    inputs.files(inputFiles())
    outputs.upToDateWhen { true }
    args = listOf("lint")
}

tasks.register<PnpmTask>("format") {
    group = "verification"
    description = "Format code using Prettier"
    dependsOn("pnpmInstall")
    inputs.files(inputFiles())
    outputs.upToDateWhen { true }
    args = listOf("format")
}

/*
 * Playwright
 */

tasks.register<PnpmTask>("playwright-install") {
    group = "verification"
    description = "Install Playwright browsers"
    dependsOn("pnpmInstall")
    outputs.dir("$rootDir/.gradle/playwright")
    environment.put("PLAYWRIGHT_BROWSERS_PATH", "$rootDir/.gradle/playwright")
    args = listOf("exec", "playwright", "install")
}

tasks.register<PnpmTask>("playwright") {
    group = "verification"
    description = "Run Playwright tests"
    dependsOn("playwright-install", ":whisper-spring-server:composeUp")
    inputs.files(inputFiles())
    outputs.upToDateWhen { true }
    environment.put("PLAYWRIGHT_BROWSERS_PATH", "$rootDir/.gradle/playwright")
    args = listOf("exec", "playwright", "test")
}

tasks.register<PnpmTask>("playwright-ui") {
    group = "verification"
    description = "Open Playwright interactive UI"
    dependsOn("playwright-install")
    environment.put("PLAYWRIGHT_BROWSERS_PATH", "$rootDir/.gradle/playwright")
    args = listOf("exec", "playwright", "test", "--ui")
}

/*
 * Build
 */

tasks.named("check").configure {
    dependsOn("svelte-check", "lint", "playwright")
}

tasks.named("build").configure {
    dependsOn("compile", "check")
}
