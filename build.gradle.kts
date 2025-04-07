plugins {
    kotlin("jvm") version "2.0.10"
    application
}

group = "br.com.woodriver"
version = "1.0-SNAPSHOT"
val gdxVersion = "1.13.1"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.badlogicgames.gdx:gdx:$gdxVersion")
    implementation("com.badlogicgames.gdx:gdx-tools:$gdxVersion")
    implementation("com.badlogicgames.gdx:gdx-backend-lwjgl3:$gdxVersion")
    implementation("com.badlogicgames.gdx:gdx-platform:$gdxVersion:natives-desktop")
    testImplementation(kotlin("test"))
}

application {
    mainClass.set("br.com.woodriver.MainKt") // Note the Kt suffix for Kotlin main class
}

tasks.test {
    useJUnitPlatform()
}