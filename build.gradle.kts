import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.HashMap

plugins {
    id("dev.isxander.modstitch.base") version "0.7.1-unstable"
    id("me.modmuss50.mod-publish-plugin") version "0.8.4"
}

// Version controller
class VersionDefinition(vararg pairs: Pair<String, String>) {
    private val versions = mapOf(*pairs)

    operator fun get(key: String): String? = versions[key]
    fun getOrDefault(key: String, default: String): String = versions[key] ?: default
}

// Dependencies version

// Fabric API
val fabricApiVersion = VersionDefinition(
    // 1.21.x
    "1.21.11" to "0.139.4+1.21.11",
    "1.21.10" to "0.135.0+1.21.10",
    "1.21.9" to "0.134.0+1.21.9",
    "1.21.8" to "0.134.0+1.21.8",
    "1.21.7" to "0.129.0+1.21.7",
    "1.21.6" to "0.128.2+1.21.6",
    "1.21.5" to "0.128.2+1.21.5",
    "1.21.4" to "0.119.4+1.21.4",
    "1.21.3" to "0.114.1+1.21.3",
    "1.21.2" to "0.106.1+1.21.2",
    "1.21.1" to "0.116.7+1.21.1",
    "1.21" to "0.102.0+1.21",
    // 1.20.x
    "1.20.6" to "0.100.8+1.20.6",
    "1.20.5" to "0.97.8+1.20.5",
    "1.20.4" to "0.97.3+1.20.4",
    "1.20.3" to "0.91.1+1.20.3",
    "1.20.2" to "0.91.6+1.20.2",
    "1.20.1" to "0.92.6+1.20.1",
    "1.20" to "0.83.0+1.20",
    // 1.19.x
    "1.19.4" to "0.87.2+1.19.4",
    "1.19.3" to "0.76.1+1.19.3",
    "1.19.2" to "0.77.0+1.19.2",
    "1.19.1" to "0.58.5+1.19.1",
    "1.19" to "0.58.0+1.19",
    // 1.18.x
    "1.18.2" to "0.77.0+1.18.2",
    "1.18.1" to "0.46.6+1.18",
    "1.18" to "0.44.0+1.18",
    // 1.17.x
    "1.17.1" to "0.46.1+1.17",
    "1.17" to "0.46.1+1.17",
    // 1.16.x
    "1.16.5" to "0.42.0+1.16",
)

// Mod Menu
val modMenuVersion = VersionDefinition(
    // 1.21.x
    "1.21.11" to "17.0.0-alpha.1",
    "1.21.10" to "16.0.0-rc.1",
    "1.21.9" to "16.0.0-rc.1",
    "1.21.8" to "15.0.0",
    "1.21.7" to "15.0.0",
    "1.21.6" to "15.0.0",
    "1.21.5" to "14.0.0-rc.2",
    "1.21.4" to "13.0.3",
    "1.21.3" to "12.0.0",
    "1.21.2" to "12.0.0",
    "1.21.1" to "11.0.3",
    "1.21" to "11.0.3",
    // 1.20.x
    "1.20.6" to "10.0.0",
    "1.20.5" to "10.0.0",
    "1.20.4" to "9.2.0",
    "1.20.3" to "9.0.0",
    "1.20.2" to "8.0.1",
    "1.20.1" to "7.2.2",
    "1.20" to "7.0.1",
    // 1.19.x
    "1.19.4" to "6.3.1",
    "1.19.3" to "5.1.0",
    "1.19.2" to "4.2.0-beta.2",
    "1.19.1" to "4.0.5",
    "1.19" to "4.0.4",
    // 1.18.x
    "1.18.2" to "3.2.5",
    "1.18.1" to "3.0.1",
    "1.18" to "3.0.0",
    // 1.17.x
    "1.17.1" to "2.0.17",
    "1.17" to "2.0.17",
    // 1.16.x
    "1.16.5" to "1.16.23",
)

// NeoForge (только для 1.20.2+)
val neoForgeVersion = VersionDefinition(
    // 1.21.x
    "1.21.11" to "21.11.3-beta",
    "1.21.10" to "21.10.63",
    "1.21.9" to "21.9.16-beta",
    "1.21.8" to "21.8.47",
    "1.21.7" to "21.7.25-beta",
    "1.21.6" to "21.6.20-beta",
    "1.21.5" to "21.5.95",
    "1.21.4" to "21.4.155",
    "1.21.3" to "21.3.93",
    "1.21.2" to "21.2.1-beta",
    "1.21.1" to "21.1.211",
    "1.21" to "21.0.167",
    // 1.20.x
    "1.20.6" to "20.6.138",
    "1.20.5" to "20.5.21-beta",
    "1.20.4" to "20.4.250",
)

val forgeDependenciesVersion = mapOf(
    // 1.21.x
    "1.21.10" to "59.0.0",
    "1.21.8" to "56.0.0",
    "1.21.5" to "55.0.0",
    "1.21.4" to "54.0.0",
    "1.21.3" to "53.0.0",
    "1.21.1" to "51.0.0",

    // 1.20.x
    "1.20.6" to "50.0.0",
    "1.20.4" to "49.0.1",
    "1.20.2" to "48.0.0",
    "1.20.1" to "46.0.14",

    // 1.19.x
    "1.19.4" to "45.4.0",
    "1.19.3" to "44.1.0",
    "1.19.2" to "42.0.9",
    "1.19" to "41.1.0",

    // 1.18.x
    "1.18.2" to "38.0.17",

    // 1.17.x
    "1.17.1" to "37.1.1",

    // 1.16.x
    "1.16.5" to "36.2.34",
)

val neoforgeDependenciesVersion = mapOf(
    "1.21.10" to "21.9.0-beta",
    "1.21.8" to "21.6.0-beta",
    "1.21.5" to "21.5.0-beta",
    "1.21.4" to "21.3.0-beta",
    "1.21.3" to "21.2.0-beta",
    "1.21.1" to "21.0.0-beta",
    "1.20.6" to "20.5.0-beta",
    "1.20.4" to "20.3.1-beta",
)

// Forge
val forgeVersion = VersionDefinition(
    // 1.20.x
    "1.21.10" to "60.1.0",
    "1.21.9" to "59.0.5",
    "1.21.8" to "58.1.0",
    "1.21.7" to "57.0.3",
    "1.21.6" to "56.0.9",
    "1.21.5" to "55.1.0",
    "1.21.4" to " 54.1.0",
    "1.21.3" to "53.1.0",
    "1.21.1" to "52.1.0",
    "1.21"   to "51.0.33",
    // 1.20.x
    "1.20.6" to "50.2.0",
    "1.20.4" to "49.2.0",
    "1.20.3" to "49.0.2",
    "1.20.2" to "48.1.0",
    "1.20.1" to "47.4.10",
    "1.20" to "46.0.14",
    // 1.19.x
    "1.19.4" to "45.4.2",
    "1.19.3" to "44.1.23",
    "1.19.2" to "43.5.1",
    "1.19.1" to "42.0.9",
    "1.19" to "41.1.0",
    // 1.18.x
    "1.18.2" to "40.3.0",
    "1.18.1" to "39.1.2",
    "1.18" to "38.0.17",
    // 1.17.x
    "1.17.1" to "37.1.1",
    // 1.16.x
    "1.16.5" to "36.2.34",
)

// Parchment Mappings
val parchmentVersion = VersionDefinition(
    // 1.21.x
    "1.21.10" to "2025.10.12",
    "1.21.9" to "2025.10.05",
    "1.21.8" to "2025.09.14",
    "1.21.7" to "2025.07.18",
    "1.21.6" to "2025.06.29",
    "1.21.5" to "2025.06.15",
    "1.21.4" to "2025.03.23",
    "1.21.3" to "2024.12.07",
    "1.21.1" to "2024.11.17",
    "1.21"   to "2024.11.10",
    // 1.20.x
    "1.20.6" to "2024.06.16",
    "1.20.4" to "2024.04.14",
    "1.20.3" to "2023.12.31",
    "1.20.2" to "2023.12.10",
    "1.20.1" to "2023.09.03",
    // 1.19.x
    "1.19.4" to "2023.06.26",
    "1.19.3" to "2023.06.25",
    "1.19.2" to "2022.11.27",
    // 1.18.x
    "1.18.2" to "2022.11.06",
    "1.18.1" to "2022.03.06",
    // 1.17.x
    "1.17.1" to "2021.12.12",
    "1.17"   to "2021.07.21",
    // 1.16.x
    "1.16.5" to "2022.03.06",
)

val additionalVersions = mapOf(
    // 1.21.x
    "1.21.11" to emptyList(),
    "1.21.10" to listOf("1.21.9"),
    "1.21.8" to listOf("1.21.6", "1.21.7"),
    "1.21.7" to emptyList(),
    "1.21.6" to emptyList(),
    "1.21.5" to emptyList(),
    "1.21.4" to emptyList(),
    "1.21.3" to listOf("1.21.2"),
    "1.21.2" to emptyList(),
    "1.21.1" to listOf("1.21"),

    // 1.20.x
    "1.20.6" to listOf("1.20.5"),
    "1.20.4" to listOf("1.20.3"),
    "1.20.2" to emptyList(),
    "1.20.1" to listOf("1.20"),

    // 1.19.x
    "1.19.4" to emptyList(),
    "1.19.3" to emptyList(),
    "1.19.2" to listOf("1.19.1"),
    "1.19.1" to emptyList(),
    "1.19" to emptyList(),

    // 1.18.x
    "1.18.2" to listOf("1.18.1", "1.18"),
    "1.18.1" to emptyList(),
    "1.18" to emptyList(),

    // 1.17.x
    "1.17.1" to listOf("1.17"),

    // 1.16.x
    "1.16.5" to emptyList(),
)

// Configs

fun cfg(name: String): String {
    return try {
        property("cfg.${name}") as String
    } catch (e: Exception) {
        property(name) as? String ?: ""
    }
}

val config = HashMap<String, String>()

val modstitch_platform = try {
    property("modstitch.platform") as String
} catch (e: Exception) {
    when {
        name.contains("fabric") -> "loom"
        name.contains("neoforge") -> "moddevgradle"
        name.contains("forge") -> "moddevgradle-legacy"
        else -> "loom"
    }
}

val is_fabric = modstitch_platform == "loom"
config["is_fabric"] = is_fabric.toString()
val is_forge = modstitch_platform == "moddevgradle-legacy"
config["is_forge"] = is_forge.toString()
val is_neoforge = modstitch_platform == "moddevgradle-regular"
config["is_neoforge"] = is_neoforge.toString()

val minecraft_version = cfg("minecraft")
config["minecraft_version"] = minecraft_version
val mod_name = cfg("name")
config["mod_name"] = mod_name
val mod_id = cfg("id")
config["mod_id"] = mod_id
val mod_version = cfg("version")
config["mod_version"] = mod_version
val mod_author = cfg("author")
config["mod_author"] = mod_author
val mod_group = if (cfg("group") == "") "com.nitsha.binds" else cfg("group")
config["mod_group"] = mod_group
val mod_description = cfg("description")
config["mod_description"] = mod_description
val mod_license = cfg("license")
config["mod_license"] = mod_license
val loader: String = name.split("-")[1]
config["loader"] = loader

val fabric_loader = when {
    stonecutter.eval(minecraft_version, ">=1.21.9") -> "0.17.2"
    else -> "0.16.14"
}
config["fabric_loader"] = fabric_loader
val fabric_version = fabricApiVersion.getOrDefault(minecraft_version, "")
config["fabric_version"] = fabric_version
val modmenu_version = modMenuVersion.getOrDefault(minecraft_version, "")
config["modmenu_version"] = modmenu_version
val forge_ver = forgeVersion[minecraft_version] ?: ""
config["forge_version"] = forge_ver
val neoforge_ver = neoForgeVersion[minecraft_version] ?: ""
config["neoforge_version"] = neoforge_ver
val parchment_ver = parchmentVersion[minecraft_version] ?: ""
config["parchment_version"] = parchment_ver

val forge_dep_ver = forgeDependenciesVersion[minecraft_version] ?: ""
val neoforge_dep_ver = neoforgeDependenciesVersion[minecraft_version] ?: ""

val forgeVersionRange = when {
    stonecutter.eval(minecraft_version, ">=1.20.2") -> "[48,)"
    stonecutter.eval(minecraft_version, ">=1.20") -> "[46,)"
    stonecutter.eval(minecraft_version, ">=1.19") -> "[45,)"
    stonecutter.eval(minecraft_version, ">=1.18") -> "[40,)"
    else -> "[36,)"
}

fun parseVersion(version: String): List<Int> {
    return version.split(".").map { it.toIntOrNull() ?: 0 }
}

fun compareVersions(v1: String, v2: String): Int {
    val parts1 = parseVersion(v1)
    val parts2 = parseVersion(v2)

    for (i in 0 until maxOf(parts1.size, parts2.size)) {
        val p1 = parts1.getOrNull(i) ?: 0
        val p2 = parts2.getOrNull(i) ?: 0
        if (p1 != p2) return p1.compareTo(p2)
    }
    return 0
}

val minecraftVersionRange = run {
    val current = minecraft_version
    val additional = additionalVersions[current] ?: emptyList()

    if (additional.isEmpty()) {
        current
    } else {
        val allVersions = listOf(current) + additional
        val minVersion = allVersions.minWithOrNull { v1, v2 -> compareVersions(v1, v2) } ?: current
        val maxVersion = allVersions.maxWithOrNull { v1, v2 -> compareVersions(v1, v2) } ?: current

        ">=$minVersion <=$maxVersion"
    }
}

val minecraftVersionRangeForge = run {
    val current = minecraft_version
    val additional = additionalVersions[current] ?: emptyList()

    if (additional.isEmpty()) {
        "[$current]"
    } else {
        val allVersions = listOf(current) + additional
        val minVersion = allVersions.minWithOrNull { v1, v2 -> compareVersions(v1, v2) } ?: current
        val maxVersion = current

        val parts = maxVersion.split(".")
        val nextPatch = when {
            parts.size == 3 -> {
                val patch = parts[2].toIntOrNull() ?: 0
                "${parts[0]}.${parts[1]}.${patch + 1}"
            }
            parts.size == 2 -> {
                "${parts[0]}.${parts[1]}.1"
            }
            else -> maxVersion
        }

        "[$minVersion,$nextPatch)"
    }
}

config["mod_forgeRange"] = forgeVersionRange
config["mod_versionRange"] = minecraftVersionRange
config["mod_versionRangeForge"] = minecraftVersionRangeForge
config["mod_icon"] = "icon.png"
config["mod_issue_tracker"] = "https://github.com/Nitsha-M/Nitsha-s-FastBind/issues"
config["mnd"] = if (loader == "neoforge") "" else "mandatory = true"
config["mfd"] = if (loader == "neoforge") neoforge_dep_ver else forge_dep_ver

val java_version = when {
    // stonecutter.eval(minecraft_version, ">=1.21.9") -> 24
    stonecutter.eval(minecraft_version, ">=1.20.5") -> 21
    stonecutter.eval(minecraft_version, ">=1.18") -> 17
    else -> 16
}
config["java_version"] = java_version.toString()

version = "${mod_version}+${stonecutter.current.version}"
group = mod_group
base.archivesName = mod_id

repositories {
    mavenCentral()
    maven("https://maven.shedaniel.me/")
    maven("https://maven.terraformersmc.com/releases/")
    maven("https://maven.wispforest.io/releases/")
    maven("https://api.modrinth.com/maven")
    maven("https://maven2.bai.lol")
    maven("https://repo.sleeping.town/")
    maven("https://maven.nucleoid.xyz")
    maven("https://maven.fabricmc.net/")
    maven("https://maven.parchmentmc.org")
    maven("https://maven.neoforged.net/releases")
}

if (stonecutter.current.isActive) {
    tasks.register("runActive") {
        group = "project"
        dependsOn("build")
    }
}

modstitch {
    minecraftVersion = minecraft_version

    parchment {
        if ((is_forge || is_neoforge) && parchment_ver.isNotEmpty()) {
            mappingsVersion = parchment_ver
        }
        minecraftVersion = minecraft_version
    }

    metadata {
        modId = mod_id
        modName = mod_name
        modVersion = "${mod_version}+$minecraft_version-$loader"
        modGroup = mod_group
        modAuthor = mod_author
        modLicense = mod_license
        modDescription = mod_description

        fun <K : Any, V : Any> MapProperty<K, V>.populate(block: MapProperty<K, V>.() -> Unit) {
            block()
        }

        replacementProperties.populate {
            put("pack_format", when {
                    stonecutter.eval(minecraft_version, ">=1.21.8") -> 64
                    stonecutter.eval(minecraft_version, ">=1.21.4") -> 46
                    stonecutter.eval(minecraft_version, ">=1.21.2") -> 41
                    stonecutter.eval(minecraft_version, ">=1.21") -> 34
                    stonecutter.eval(minecraft_version, ">=1.20.5") -> 32
                    stonecutter.eval(minecraft_version, ">=1.20.3") -> 22
                    stonecutter.eval(minecraft_version, ">=1.20") -> 15
                    stonecutter.eval(minecraft_version, ">=1.19.4") -> 13
                    stonecutter.eval(minecraft_version, ">=1.19.3") -> 12
                    stonecutter.eval(minecraft_version, ">=1.19") -> 10
                    stonecutter.eval(minecraft_version, ">=1.18") -> 9
                    stonecutter.eval(minecraft_version, ">=1.17") -> 7
                    stonecutter.eval(minecraft_version, ">=1.16.2") -> 6
                    else -> 5
                }.toString()
            )
            for (entry in config.entries)
                put(entry.key, entry.value)
        }
    }

    if (is_fabric) loom {
        fabricLoaderVersion.set(fabric_loader)

        configureLoom {
            runConfigs.configureEach {
                ideConfigGenerated(true)
                vmArgs.add("-Dmixin.debug.export=true")
            }
        }
    }

    // ModDevGradle (NeoForge, Forge)
    moddevgradle {
        if (isModDevGradleLegacy && forge_ver.isNotEmpty()) {
            forgeVersion = "${minecraft_version}-${forge_ver}"
        }
        if (isModDevGradleRegular && neoforge_ver.isNotEmpty()) {
            neoForgeVersion = neoforge_ver
        }

        defaultRuns()

        configureNeoForge {
            validateAccessTransformers = false

            runs.all { disableIdeRun() }
        }

        onEnable {
            tasks.named("createMinecraftArtifacts") {
                dependsOn("stonecutterGenerate")
            }
        }
    }

    mixin {
        addMixinsToModManifest = true
        configs.register(mod_id)
    }
}

java {
    withSourcesJar()
    targetCompatibility = JavaVersion.toVersion(java_version)
    sourceCompatibility = JavaVersion.toVersion(java_version)
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(java_version))
    }
}

sourceSets.main {
    java.srcDir("../src/main/java")
    resources.srcDir("../src/main/resources")

    modstitch.templatesSourceDirectorySet.srcDir("../src/main/templates")
}

stonecutter {
    constants.match(
        loader,
        "fabric",
        "neoforge",
        "forge"
    )

    fun swap(name: String, value: String) {
        swaps["${name}_string"] = "\"${value}\""
        swaps[name] = value
    }

    for (entry in config.entries)
        swap(entry.key, entry.value)

}

dependencies {
    modstitch.loom {
        if (is_fabric) {
            modstitchModImplementation("net.fabricmc.fabric-api:fabric-api:${fabric_version}")
            modstitchModImplementation("com.terraformersmc:modmenu:${modmenu_version}")
        }
        if (is_neoforge)
            modstitchModImplementation("net.neoforged:neoforge:${neoforge_ver}")
    }
}

tasks {
    processResources {
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        outputs.upToDateWhen { false }
    }

    withType<JavaCompile> {
        dependsOn("stonecutterGenerate")
    }

    withType<JavaCompile>().configureEach {
        options.encoding = "UTF-8"
        options.release.set(java_version)
    }
}

modstitch.onEnable {
    modstitch.moddevgradle {
        tasks.named("createMinecraftArtifacts") {
            dependsOn("stonecutterGenerate")
        }
    }

    val finalJarTasks = listOf(
        modstitch.finalJarTask
    )

    if (stonecutter.eval(minecraft_version, "<=1.19.2") && is_forge) {
        tasks.register("createDummyMixinMappings") {
            doLast {
                val mixinDir = file("build/mixin")
                mixinDir.mkdirs()
                val mappingsFile = file("build/mixin/${mod_id}.refmap.json.mappings.tsrg")
                if (!mappingsFile.exists()) {
                    mappingsFile.writeText("")
                }
            }
        }

        tasks.named("reobfJar") {
            dependsOn("createDummyMixinMappings")
        }
    }

    tasks.register<Copy>("buildAndCollect") {
        group = "build"

        finalJarTasks.forEach { jar ->
            dependsOn(jar)
            from(jar.flatMap { it.archiveFile })
        }

        into(rootProject.layout.buildDirectory.file("libs/${mod_version}"))
        dependsOn("build")
    }
}


val additional = additionalVersions[stonecutter.current.version] ?: emptyList()

publishMods {
    modstitch.onEnable {
        file = modstitch.finalJarTask.flatMap { it.archiveFile }
    }
    changelog = file("../../changelog/${mod_version}.md").takeIf { it.exists() }?.readText() ?: "No changelog available"
    displayName = "${mod_name} ${mod_version} — MC${stonecutter.current.version} [${loader.uppercase()}]"
    type = STABLE
    version = "${mod_version}-${stonecutter.current.version}-${loader}"
    modLoaders.add(loader)

    modrinth {
        projectId = "v0yaXjcg"
        accessToken = providers.environmentVariable("MODRINTH_TOKEN")
        minecraftVersions.add(stonecutter.current.version)
        additional.forEach { minecraftVersions.add(it) }

    }

    curseforge {
        projectId = "1304893"
        accessToken = providers.environmentVariable("CURSEFORGE_TOKEN")
        minecraftVersions.add(stonecutter.current.version)
        additional.forEach { minecraftVersions.add(it) }

    }
}