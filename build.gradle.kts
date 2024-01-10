import org.gradle.kotlin.dsl.main
import org.gradle.kotlin.dsl.shadow
import org.gradle.kotlin.dsl.sourceSets

plugins {
    idea
    java
    id("gg.essential.loom") version "0.10.0.+"
    id("dev.architectury.architectury-pack200") version "0.1.3"
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("net.kyori.blossom") version "1.3.1"
    "dev.lynith.multiversion.part"
}

val version: String by project
val mod_name: String by project
val mod_id: String by project
val archiveBaseName: String by project

// Toolchains:
java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(8))
}

blossom {
    replaceToken("@VER@", version)
    replaceToken("@NAME@", mod_name)
    replaceToken("@ID@", mod_id)
}


// Minecraft configuration:
loom {
    log4jConfigs.from(file(".gradle/loom-cache/log4j.xml"))
    launchConfigs {
        "client" {
            property("mixin.debug", "true")
            property("asmhelper.verbose", "true")
            arg("--tweakClass", "gg.essential.loader.stage0.EssentialSetupTweaker")
            arg("--mixin", "mixins.${mod_id}.json")
        }
    }
    forge {
        pack200Provider.set(dev.architectury.pack200.java.Pack200Adapter())
        mixinConfig("mixins.${mod_id}.json")
    }
    mixin {
        defaultRefmapName.set("mixins.${mod_id}.refmap.json")
    }
}

sourceSets.main {
    output.setResourcesDir(file("$buildDir/classes/java/main"))
}

tasks.withType(JavaCompile::class) {
    options.encoding = "windows-1252"
}

repositories {
    mavenCentral()
    maven("https://repo.spongepowered.org/maven/")
    maven("https://repo.sk1er.club/repository/maven-public/")
    maven("https://repo.sk1er.club/repository/maven-releases/")
    maven("https://jitpack.io")
}

val shadowImpl: Configuration by configurations.creating {
    configurations.implementation.get().extendsFrom(this)
}

val exportLib: Configuration by configurations.creating {
    configurations.compileOnly.get().extendsFrom(this)
}

dependencies {
    minecraft("com.mojang:minecraft:1.8.9")
    mappings("de.oceanlabs.mcp:mcp_stable:22-1.8.9")
    forge("net.minecraftforge:forge:1.8.9-11.15.1.2318-1.8.9")

    compileOnly(libs.mixin)

    shadowImpl("org.reflections:reflections:0.10.2")
    //maybe remove long term
    shadowImpl("gg.essential:loader-launchwrapper:1.2.1")
}

tasks.withType(JavaCompile::class) {
    //options.encoding = "windows-1252"
}

tasks.withType(Jar::class) {
    manifest.attributes.run {
        this["FMLCorePluginContainsFMLMod"] = "true"
        this["ForceLoadAsMod"] = "true"

        this["TweakClass"] = "gg.essential.loader.stage0.EssentialSetupTweaker"
        this["MixinConfigs"] = "mixins.${mod_id}.json"
    }
}

tasks.processResources {
    inputs.property("version", project.version)
    inputs.property("id", mod_id)
    inputs.property("name", mod_name)

    filesMatching(listOf("mcmod.info", "mixins.${mod_id}.json")) {
        expand(inputs.properties)
    }
}


val remapJar by tasks.named<net.fabricmc.loom.task.RemapJarTask>("remapJar") {
    archiveClassifier.set("")
    from(tasks.shadowJar)
    input.set(tasks.shadowJar.get().archiveFile)
}

tasks.jar {
    archiveClassifier.set("without-deps")
    destinationDirectory.set(layout.buildDirectory.dir("badjars"))
}

tasks.shadowJar {
    destinationDirectory.set(layout.buildDirectory.dir("badjars"))
    archiveClassifier.set("all-dev")
    configurations = listOf(shadowImpl)
    doLast {
        configurations.forEach {
            println("Copying jars into mod: ${it.files}")
        }
    }
}

tasks.assemble.get().dependsOn(tasks.remapJar)
