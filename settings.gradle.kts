pluginManagement {
    val kotlinVersion: String by settings
    val quarkusPlatformVersion: String by settings
    plugins {
        kotlin("jvm") version kotlinVersion
        kotlin("plugin.allopen") version kotlinVersion
        id("io.quarkus") version quarkusPlatformVersion
    }
    repositories {
        mavenLocal()
        mavenCentral()
        gradlePluginPortal()
    }
}
rootProject.name="doc-l10n-kit"
