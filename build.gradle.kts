import java.io.ByteArrayOutputStream
import org.gradle.api.tasks.bundling.Jar
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    kotlin("jvm") version "2.2.0"
    `java-library`
    `maven-publish`
}

val groupId = "no.iktdev.library"
val artifactName = "subtitle"
val fallbackVersion = "1.8.1-SNAPSHOT"

group = groupId
version = resolveProjectVersion()

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.google.code.gson:gson:2.9.0")
    implementation("org.json:json:20220320")

    testImplementation("junit:junit:4.13.2")
    testImplementation(platform("org.junit:junit-bom:5.11.4"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
    testRuntimeOnly("org.junit.vintage:junit-vintage-engine")
    testImplementation("org.assertj:assertj-core:3.27.7")
}

kotlin {
    jvmToolchain(21)
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_21)
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
    withSourcesJar()
    withJavadocJar()
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<Jar>().configureEach {
    manifest {
        attributes(
            mapOf(
                "Implementation-Title" to artifactName,
                "Implementation-Version" to project.version.toString(),
                "Implementation-Vendor" to "no.iktdev"
            )
        )
    }
}

val snapshotRepositoryUrl = providers.environmentVariable("REPOSILITE_SNAPSHOT_URL")
    .orElse("https://reposilite.iktdev.no/snapshots").get()
val releaseRepositoryUrl = providers.environmentVariable("REPOSILITE_RELEASE_URL")
    .orElse("https://reposilite.iktdev.no/releases").get()
val publicationRepositoryUrl = if (version.toString().endsWith("-SNAPSHOT")) {
    snapshotRepositoryUrl
} else {
    releaseRepositoryUrl
}

publishing {
    publications {
        create<MavenPublication>("reposilite") {
            groupId = project.group.toString()
            artifactId = artifactName
            version = project.version.toString()
            from(components["java"])

            pom {
                name.set(artifactName)
                description.set("Streamit Subtitle Convert Library")
                url.set("https://reposilite.iktdev.no")
            }
        }
    }

    repositories {
        mavenLocal()
        maven {
            name = "reposilite"
            url = uri(publicationRepositoryUrl)
            credentials {
                username = providers.environmentVariable("REPOSILITE_USERNAME")
                    .orElse(providers.environmentVariable("reposiliteUsername")).orNull
                password = providers.environmentVariable("REPOSILITE_PASSWORD")
                    .orElse(providers.environmentVariable("reposilitePassword")).orNull
            }
        }
    }
}

fun resolveProjectVersion(): String {
    providers.gradleProperty("releaseVersion").orNull?.let { return normalizeTagVersion(it) }
    providers.environmentVariable("RELEASE_VERSION").orNull?.let { return normalizeTagVersion(it) }

    val ref = sequenceOf("GITHUB_REF", "GITEA_REF", "CI_COMMIT_TAG")
        .mapNotNull { providers.environmentVariable(it).orNull }
        .firstOrNull { it.isNotBlank() }
    if (ref?.startsWith("refs/tags/") == true) {
        return normalizeTagVersion(ref.removePrefix("refs/tags/"))
    }

    val latestTag = gitOutput("describe", "--tags", "--abbrev=0")
    return latestTag?.let(::nextSnapshotVersion) ?: fallbackVersion
}

fun normalizeTagVersion(tag: String): String {
    val normalized = tag.trim().removePrefix("v")
    require(Regex("\\d+\\.\\d+\\.\\d+(?:[-+][0-9A-Za-z.-]+)?").matches(normalized)) {
        "Release tag '$tag' must be a semantic version such as v1.2.3"
    }
    return normalized
}

fun nextSnapshotVersion(tag: String): String {
    val match = Regex("^(?:v)?(\\d+)\\.(\\d+)\\.(\\d+)(?:[-+].*)?$").matchEntire(tag.trim())
        ?: return fallbackVersion
    val major = match.groupValues[1]
    val minor = match.groupValues[2]
    val patch = match.groupValues[3].toInt() + 1
    return "$major.$minor.$patch-SNAPSHOT"
}

fun gitOutput(vararg arguments: String): String? = try {
    val output = ByteArrayOutputStream()
    val result = exec {
        commandLine = listOf("git", *arguments)
        standardOutput = output
        isIgnoreExitValue = true
    }
    output.toString().trim().takeIf { result.exitValue == 0 && it.isNotBlank() }
} catch (_: Exception) {
    null
}
