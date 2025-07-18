plugins {
    id("fabric-loom") version "1.10-SNAPSHOT"
    id("me.modmuss50.mod-publish-plugin") version "0.8.4"
}

class ModData {
    val id = property("mod.id").toString()
    val version = property("mod.version").toString()
    val group = property("mod.group").toString()
}

val mod = ModData()

version = "${mod.version}+${stonecutter.current.version}"
group = mod.group
base.archivesName = mod.id as String

repositories {
    mavenCentral()
    maven ( "https://maven.shedaniel.me/" )
    maven ( "https://maven.terraformersmc.com/releases/" )
    maven ( "https://maven.wispforest.io/releases/" )
    maven ( "https://api.modrinth.com/maven")
    maven ( "https://maven2.bai.lol" )
    maven ("https://repo.sleeping.town/" )
    maven ("https://maven.nucleoid.xyz" )
    maven("https://maven.fabricmc.net/")
}

if (stonecutter.current.isActive) {
    tasks.register("runActive") {
        group = "project"
        dependsOn("build")
    }
}

loom {
    // splitEnvironmentSourceSets()

    loom.runConfigs.named("client") {
        ideConfigGenerated(true)
        runDir = "../../run"
    }
}

fabricApi {
    configureDataGeneration {
        client = true
    }
}

dependencies {
    // To change the versions see the gradle.properties file
    minecraft("com.mojang:minecraft:${findProperty("minecraft_version")}")
    mappings("net.fabricmc:yarn:${findProperty("yarn_version")}:v2")
    modImplementation("net.fabricmc:fabric-loader:${findProperty("loader_version")}")

    modImplementation("net.fabricmc.fabric-api:fabric-api:${findProperty("fabric_version")}")

    modApi("com.terraformersmc:modmenu:${findProperty("modmenu_version")}")
}

val javaversion = if (stonecutter.eval(stonecutter.current.version, ">=1.20.5"))
    JavaVersion.VERSION_21 else JavaVersion.VERSION_17

tasks.withType<JavaCompile>().configureEach {
    options.release.set(javaversion.toString().toInt())
}

java {
    withSourcesJar()

    sourceCompatibility = javaversion
    targetCompatibility = javaversion
}

tasks.register<Copy>("buildAndCollect") {
    group = "build"
    from(tasks.remapJar.get().archiveFile)
    into(rootProject.layout.buildDirectory.file("libs/${mod.version}"))
    dependsOn("build")
}

tasks.processResources {
    val props = buildMap {
        put("id", project.property("mod.id"))
        put("version", project.property("mod.version"))
        put("minecraft", project.property("minecraft_version"))
        put("fabricloader", project.property("loader_version"))
        put("fabricapi", project.property("fabric_version"))
        put("compatibility_level", "JAVA_${javaversion.ordinal + 1}")
    }
    filteringCharset = "UTF-8"

    filesMatching(listOf("fabric.mod.json", "*.mixins.json")) {
        expand(props)
    }
    inputs.properties(props)
}

publishMods {
    file = tasks.remapJar.get().archiveFile
    changelog = file("../../changelog/${mod.version}.md").readText()
    displayName = "Nitsha's FastBind ${mod.version} — MC${stonecutter.current.version}"
    type = STABLE
    version = "${mod.version}-${stonecutter.current.version}";
    modLoaders.add("fabric")

    modrinth {
        projectId = "v0yaXjcg"
        accessToken = providers.environmentVariable("MODRINTH_TOKEN")
        minecraftVersions.add(stonecutter.current.version)
        requires("fabric-api")
    }

    curseforge {
        projectId = "1304893"
        accessToken = providers.environmentVariable("CURSEFORGE_TOKEN")
        minecraftVersions.add(stonecutter.current.version)
        requires("fabric-api")
    }
}