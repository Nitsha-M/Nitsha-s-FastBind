pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()

        // Loom platform
        maven("https://maven.fabricmc.net/")

        // MDG platform
        maven("https://maven.neoforged.net/releases/")

        // Stonecutter
        maven("https://maven.kikugie.dev/releases")
        maven("https://maven.kikugie.dev/snapshots")

        // Modstitch
        maven("https://maven.isxander.dev/releases")

        // Parchment
        maven("https://maven.parchmentmc.org")
    }
}

plugins {
    id("dev.kikugie.stonecutter") version "0.7.10"
}

stonecutter {
    kotlinController = true
    centralScript = "build.gradle.kts"

    create(rootProject) {
        fun mc(mcVersion: String, name: String = mcVersion, loaders: Iterable<String>) =
            loaders.forEach { version("$name-$it", mcVersion) }

        val loaders_1 = listOf("fabric", "forge")
        val loaders_1_1 = listOf("fabric")
        val loaders_2 = listOf("fabric", "neoforge")
        // 1.21
        mc("1.21.9", loaders = loaders_2)
        mc("1.21.8", loaders = loaders_2)
        mc("1.21.7", loaders = loaders_2)
        mc("1.21.6", loaders = loaders_2)
        mc("1.21.5", loaders = loaders_2)
        mc("1.21.4", loaders = loaders_2)
        mc("1.21.3", loaders = loaders_2)
        mc("1.21.2", loaders = loaders_2)
        mc("1.21.1", loaders = loaders_2)

        // 1.20
        mc("1.20.6", loaders = loaders_2)
        mc("1.20.4", loaders = loaders_2)
        mc("1.20.2", loaders = loaders_1_1)
        mc("1.20.1", loaders = loaders_1)

        // 1.19
        mc("1.19.4", loaders = loaders_1)
        mc("1.19.3", loaders = loaders_1)
        mc("1.19.2", loaders = loaders_1)
        mc("1.19.1", loaders = loaders_1)
        mc("1.19", loaders = loaders_1)

        // 1.18
        mc("1.18.2", loaders = loaders_1)
        mc("1.18.1", loaders = loaders_1)
        mc("1.18", loaders = loaders_1)

        // 1.17
        mc("1.17.1", loaders = loaders_1)

        // 1.16
        mc("1.16.5", loaders = loaders_1_1)

        vcsVersion = "1.21.3-fabric"
    }
}

rootProject.name = extra["cfg.name"] as String