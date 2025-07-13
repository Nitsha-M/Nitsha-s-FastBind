plugins {
    id("fabric-loom") version "1.10-SNAPSHOT"
    id("maven-publish")
}

class ModData {
    val id = property("mod.id").toString()
    val version = property("mod.version").toString()
    val group = property("mod.group").toString()
}

val mod = ModData()

version = "${mod.version}+${stonecutter.current.version}"
group = mod.group

base { archivesName.set(mod.id) }

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

java {
    withSourcesJar()
    val java = if (stonecutter.eval(stonecutter.current.version, ">=1.20.5"))
        JavaVersion.VERSION_21 else JavaVersion.VERSION_17
    targetCompatibility = java
    sourceCompatibility = java
}

var javaVer = "17"
if (stonecutter.eval(stonecutter.current.version, ">=1.20.5")) {
    javaVer = "21"
}

tasks.compileJava {
    sourceCompatibility = "17"
    targetCompatibility = javaVer
    options.encoding = "UTF-8"
}

tasks.processResources {
    val props = buildMap {
        put("id", project.property("mod.id"))
        put("version", project.property("mod.version"))
        put("minecraft", project.property("minecraft_version"))
        put("fabricloader", project.property("loader_version"))
        put("fabricapi", project.property("fabric_version"))
    }
    filteringCharset = "UTF-8"

    filesMatching("fabric.mod.json") { expand(props) }
}

tasks.register<Copy>("copyJars") {
    dependsOn(subprojects.map { it.tasks.named("build") })
    from(subprojects.map { it.layout.buildDirectory.dir("libs") })
    into(rootDir.resolve("build/all-jars"))
    include("*.jar")
}

tasks.named("build") {
    finalizedBy("copyJars")
}