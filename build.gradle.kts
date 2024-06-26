import java.io.ByteArrayOutputStream

plugins {
    id("java")
    id("io.github.goooler.shadow") version "8.1.7"
}

group = "org.sgrewritten"
version = "0.1.1-ALPHA"

repositories {
    mavenCentral()
    mavenLocal()
    maven("https://jitpack.io")
    maven("https://repo.codemc.io/repository/maven-snapshots/")
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    testImplementation("com.github.seeseemelk:MockBukkit-v1.20:3.86.0")
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.2")
    testImplementation("net.joshka:junit-json-params:5.10.2-r0")
    testImplementation("org.eclipse.parsson:parsson:1.1.6")
    implementation("io.papermc.paper:paper-api:1.20.4-R0.1-SNAPSHOT")
    implementation("org.jetbrains:annotations:24.1.0")
    implementation("org.sgrewritten:stargate:1.0.0.16-NIGHTLY")
    implementation("net.wesjd:anvilgui:1.9.4-SNAPSHOT")
}

tasks {
    compileJava {
        options.encoding = Charsets.UTF_8.name()
        options.release.set(21)
    }

    processResources {
        filesMatching("**/plugin.yml") {
            expand(project.properties)
        }
    }

    jar {
        manifest {
            attributes["paperweight-mappings-namespace"] = "spigot"
        }
    }

    shadowJar {
        dependencies {
            include(dependency("net.wesjd:anvilgui"))
        }
        relocate("net.wesjd.anvilgui", project.group.toString() + "." + project.name + ".anvilgui")
        archiveClassifier.set("")
        archiveBaseName = "StargateMechanics"
        archiveClassifier = runCommand("git", "rev-parse", "HEAD").subSequence(0,7).toString()
    }

    test {
        useJUnitPlatform()
    }

    register("install") {
        dependsOn("shadowJar")
    }
}

fun runCommand(vararg cmd: String): String {
    val stdout = ByteArrayOutputStream()
    exec {
        commandLine(*cmd)
        standardOutput = stdout
    }
    return stdout.toString().trim()
}