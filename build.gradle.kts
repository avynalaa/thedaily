// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.4.0" apply false
    id("org.jetbrains.kotlin.android") version "2.1.0" apply false
    id("org.jetbrains.kotlin.kapt") version "2.1.0" apply false
    id("org.jetbrains.kotlin.jvm") version "2.1.0" apply false
    id("org.jetbrains.kotlin.plugin.compose") version "2.1.0" apply false
    id("org.jetbrains.kotlin.plugin.serialization") version "2.1.0" apply false
}


subprojects {
    afterEvaluate {
        dependencies {
            // Add test dependencies for serialization and JUnit
            add("testImplementation", "org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")
            add("testImplementation", "org.jetbrains.kotlin:kotlin-test:2.1.0")
            add("testImplementation", "junit:junit:4.13.2")
        }
    }
}
