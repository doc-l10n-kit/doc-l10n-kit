pluginManagement {
    val quarkusPlatformVersion: String by settings
    plugins {
        id("io.quarkus") version quarkusPlatformVersion
    }
    repositories {
        mavenLocal()
        mavenCentral()
        gradlePluginPortal()
    }
}
rootProject.name="doc-l10n-kit"
