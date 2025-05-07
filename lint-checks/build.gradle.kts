plugins {
    id("java-library")
    alias(libs.plugins.jetbrains.kotlin.jvm)
    alias(libs.plugins.ktlint)
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

dependencies {
    compileOnly(libs.bundles.lintCheck)
    compileOnly(libs.kotlinStdLib)
    testImplementation(libs.bundles.lintTest)
}

tasks.jar {
    manifest {
        attributes(mapOf("Lint-Registry-v2" to "com.kyrie.myportfolio.lintChecks.MyIssueRegistry"))
    }
}
