plugins {
    id("java-library")
    id("xyz.jpenilla.run-paper") version "3.1.0"
    id("com.gradleup.shadow") version "9.2.2"
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.9-R0.1-SNAPSHOT")
    implementation("tools.jackson.core:jackson-databind:3.1.7") // 3.1 is latest LTS as per https://github.com/FasterXML/jackson/wiki/Jackson-Releases
    implementation("dev.failsafe:failsafe:3.3.2")
}

java {
    toolchain.languageVersion = JavaLanguageVersion.of(21)
}

val testVersion = providers.gradleProperty("mcVersion").orElse("1.21.9")

tasks {
    shadowJar {
        archiveClassifier.set("")
        mergeServiceFiles()
        relocate("tools.jackson", "me.amgelo563.easy_sprites.libs.jackson")
        relocate("com.fasterxml.jackson.annotation", "me.amgelo563.easy_sprites.libs.jackson.annotation")
    }
    build { dependsOn(shadowJar) }

    runServer {
        val version = testVersion.get()
        minecraftVersion(version)
        runDirectory = layout.projectDirectory.dir("run/$version")

        val javaVersion = if (version.startsWith("1.")) 21 else 25
        javaLauncher = project.javaToolchains.launcherFor {
            languageVersion = JavaLanguageVersion.of(javaVersion)
        }

        jvmArgs("-Xms2G", "-Xmx2G", "-Dcom.mojang.eula.agree=true")
    }

    processResources {
        val props = mapOf("version" to version, "description" to project.description)
        filesMatching("plugin.yml") {
            expand(props)
        }
    }
}
