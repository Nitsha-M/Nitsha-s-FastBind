plugins {
    id("dev.kikugie.stonecutter")
    id("me.modmuss50.mod-publish-plugin") version "0.8.4" apply false
}

stonecutter active "1.21.3" /* [SC] DO NOT EDIT */

stonecutter tasks {
    order("publishModrinth")
    order("publishCurseforge")
}