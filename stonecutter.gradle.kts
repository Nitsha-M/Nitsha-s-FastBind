plugins {
    base
    kotlin("jvm") version "2.1.0" apply false
    id("dev.kikugie.stonecutter")
    id("me.modmuss50.mod-publish-plugin") version "0.8.4" apply false
    val modstitchVersion = "0.7.1-unstable"
    id("dev.isxander.modstitch.base") version modstitchVersion apply false
}

stonecutter active "1.21.3-fabric" /* [SC] DO NOT EDIT */

allprojects {
    repositories {
        mavenCentral()
        mavenLocal()
        maven("https://maven.neoforged.net/releases")
        maven("https://maven.fabricmc.net/")
        maven("https://maven.parchmentmc.org")
        maven("https://maven.isxander.dev/releases")
        maven("https://maven.isxander.dev/snapshots")
    }
}

stonecutter tasks {
    order("publishModrinth")
    order("publishCurseforge")
}