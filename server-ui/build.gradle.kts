import com.github.gradle.node.pnpm.task.PnpmTask
import groovy.json.JsonSlurper

plugins {
    alias(libs.plugins.nodeGradle)
}

@Suppress("UNCHECKED_CAST")
val packageJson = JsonSlurper().parse(file("package.json")) as Map<String, Any>

fun Map<String, Any>.getIn(path: String): String? =
    path
        .split(".")
        .fold(this as Any?) { acc, key ->
            (acc as? Map<*, *>)?.get(key)
        }?.toString()
val nodePkgVersion: String? = packageJson.getIn("devEngines.runtime.version")
val pnpmPkgVersion: String? = packageJson.getIn("devEngines.packageManager.version")

/*
 * Node
 */

node {
    version = nodePkgVersion
    pnpmVersion = pnpmPkgVersion
    download = true
    workDir = file("$rootDir/.gradle/nodejs")
    pnpmWorkDir = file("$rootDir/.gradle/pnpm")
}

fun Project.inputFiles() =
    listOf(
        fileTree("$projectDir/e2e") { include("**/*") },
        fileTree("$projectDir/src") { include("**/*") },
        fileTree("$projectDir/static") { include("**/*") },
        file("$projectDir/eslint.config.js"),
        file("$projectDir/.prettierrc"),
        file("$projectDir/.prettierignore"),
        file("$projectDir/package.json"),
        file("$projectDir/playwright.config.ts"),
        file("$projectDir/pnpm-lock.yaml"),
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

/*
 * Check
 */

tasks.register<PnpmTask>("formatCheck") {
    group = "verification"
    description = "Format code using Prettier"
    dependsOn("pnpmInstall")
    inputs.files(inputFiles())
    outputs.upToDateWhen { true }
    args = listOf("format:check")
}

tasks.register<PnpmTask>("format") {
    group = "verification"
    description = "Format code using Prettier"
    dependsOn("pnpmInstall")
    inputs.files(inputFiles())
    outputs.upToDateWhen { true }
    args = listOf("format")
}

tasks.register<PnpmTask>("lintCheck") {
    group = "verification"
    description = "Run ESLint to check code quality"
    dependsOn("pnpmInstall")
    inputs.files(inputFiles())
    outputs.upToDateWhen { true }
    args = listOf("lint:check")
}

tasks.register<PnpmTask>("lint") {
    group = "verification"
    description = "Run ESLint to check code quality"
    dependsOn("pnpmInstall")
    inputs.files(inputFiles())
    outputs.upToDateWhen { true }
    args = listOf("lint")
}

tasks.register<PnpmTask>("svelteCheck") {
    group = "verification"
    description = "Run Svelte type checking"
    dependsOn("pnpmInstall")
    inputs.files(inputFiles())
    outputs.upToDateWhen { false }
    args = listOf("svelte:check")
}

/*
 * Playwright
 */

tasks.register<PnpmTask>("playwrightInstall") {
    group = "verification"
    description = "Install Playwright browsers"
    dependsOn("pnpmInstall")
    outputs.dir("$rootDir/.gradle/playwright")
    environment.put("PLAYWRIGHT_BROWSERS_PATH", "$rootDir/.gradle/playwright")
    args = listOf("playwright-install")
}

tasks.register<PnpmTask>("playwright") {
    group = "verification"
    description = "Run Playwright tests"
    dependsOn("playwrightInstall", ":server:composeUp")
    finalizedBy(":server:composeDown")
    inputs.files(inputFiles())
    outputs.upToDateWhen { true }
    environment.put("PLAYWRIGHT_BROWSERS_PATH", "$rootDir/.gradle/playwright")
    args = listOf("playwright")
}

tasks.register<PnpmTask>("playwrightUi") {
    group = "verification"
    description = "Open Playwright interactive UI"
    dependsOn("playwrightInstall")
    environment.put("PLAYWRIGHT_BROWSERS_PATH", "$rootDir/.gradle/playwright")
    args = listOf("playwright-ui")
}

/*
 * Build
 */

tasks.named("check").configure {
    dependsOn("formatCheck", "lintCheck", "svelteCheck", "playwright")
}

tasks.named("build").configure {
    dependsOn("compile", "check")
}

/*
 * Dependency Updates
 */

tasks.register<PnpmTask>("nodeUpdate") {
    description = "Updates Node.js to the latest version"
    args = listOf("runtime", "set", "node", "latest")
}

tasks.register<PnpmTask>("pnpmUpdate") {
    description = "Updates pnpm to the latest version"
    environment.put("PNPM_CONFIG_MINIMUM_RELEASE_AGE", "10080")
    args = listOf("self-update")
}

tasks.register<PnpmTask>("updateDependencies") {
    description = "Updates all node dependencies to their latest versions"
    dependsOn("nodeUpdate", "pnpmUpdate")
    args = listOf("update", "--latest")
}
