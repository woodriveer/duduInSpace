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
    implementation("com.badlogicgames.gdx:gdx-freetype:$gdxVersion")
    implementation("com.badlogicgames.gdx:gdx-freetype-platform:$gdxVersion:natives-desktop")
    implementation("com.badlogicgames.gdx-controllers:gdx-controllers-core:2.2.3")
    implementation("com.badlogicgames.gdx-controllers:gdx-controllers-desktop:2.2.3")
    testImplementation(kotlin("test"))
}

application {
    mainClass.set("br.com.woodriver.MainKt") // Note the Kt suffix for Kotlin main class
    applicationDefaultJvmArgs = listOf("-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005")
}

tasks.test {
    useJUnitPlatform()
}