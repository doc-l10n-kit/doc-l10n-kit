plugins {
    kotlin("jvm")
    kotlin("plugin.allopen")
    id("io.quarkus")
}

repositories {
    mavenLocal()
    mavenCentral()
}

val quarkusPlatformGroupId: String by project
val quarkusPlatformArtifactId: String by project
val quarkusPlatformVersion: String by project
val docl10nkitVersion: String by project


val deepl4jVersion = "0.1.2.RELEASE"
val jgettextVersion = "0.15.1"
val asciidoctorjVersion = "2.5.8"
val jsoupVersion = "1.15.3"
val deeplJavaVersion = "1.0.1"
val assertJVersion = "3.24.2"

dependencies {
    implementation(platform("${quarkusPlatformGroupId}:${quarkusPlatformArtifactId}:${quarkusPlatformVersion}"))
    implementation("io.quarkus:quarkus-rest-client-jackson")
    implementation("io.quarkus:quarkus-picocli")
    implementation("io.quarkus:quarkus-kotlin")
    implementation("io.quarkus:quarkus-config-yaml")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("io.quarkus:quarkus-arc")
    implementation("io.quarkus:quarkus-resteasy")

    implementation("com.deepl.api:deepl-java:${deeplJavaVersion}")

    implementation("org.fedorahosted.tennera:jgettext:${jgettextVersion}")
    implementation("org.asciidoctor:asciidoctorj:${asciidoctorjVersion}")
    implementation("org.jsoup:jsoup:${jsoupVersion}")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    testImplementation("io.quarkus:quarkus-junit5")
    testImplementation("org.mockito:mockito-junit-jupiter")
    testImplementation("org.assertj:assertj-core:${assertJVersion}")
}

group = "net.sharplab.translator"
version = docl10nkitVersion

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

quarkus {
    setFinalName("doc-l10n-kit")
}

allOpen {
    annotation("javax.ws.rs.Path")
    annotation("javax.enterprise.context.ApplicationScoped")
    annotation("io.quarkus.test.junit.QuarkusTest")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = JavaVersion.VERSION_11.toString()
    kotlinOptions.javaParameters = true
}