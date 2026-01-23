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
    workDir = file("${rootDir}/.gradle/nodejs")
    pnpmWorkDir = file("${rootDir}/.gradle/pnpm")
}

/*
 * Clean
 */

tasks.register<Delete>("cleanSvelte") {
    group = "build"
    description = "Clean Svelte-specific build artifacts and dependencies"
    delete("${projectDir}/node_modules")
    delete("${projectDir}/.svelte-kit")
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
    args = listOf("build")
}

tasks.register<PnpmTask>("svelte-check") {
    group = "verification"
    description = "Run Svelte type checking"
    dependsOn("pnpmInstall")
    args = listOf("check")
}

/*
 * Check
 */

tasks.register<PnpmTask>("lint") {
    group = "verification"
    description = "Run ESLint to check code quality"
    dependsOn("pnpmInstall")
    args = listOf("lint")
}

tasks.register<PnpmTask>("format") {
    group = "verification"
    description = "Format code using Prettier"
    dependsOn("pnpmInstall")
    args = listOf("format")
}

/*
 * Playwright
 */

tasks.register<PnpmTask>("playwright-install") {
    group = "verification"
    description = "Install Playwright browsers"
    dependsOn("pnpmInstall")
    args = listOf("exec", "playwright", "install")
}

tasks.register<PnpmTask>("playwright") {
    group = "verification"
    description = "Run Playwright tests"
    dependsOn("playwright-install")
    args = listOf("exec", "playwright", "test")
}

/*
 * Build
 */

tasks.named("check").configure {
    dependsOn("svelte-check", "format", "lint", "playwright")
}

tasks.named("build").configure {
    dependsOn("compile", "check")
}
