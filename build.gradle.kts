// build.gradle.kts

import org.jetbrains.kotlin.gradle.dsl.Coroutines
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

group = "org.example"
version = "1.0-SNAPSHOT"

val ktor_version = "0.9.5"
val jackson_version = "2.9.2"

plugins {
    application
    kotlin("jvm") version "1.2.71"
}

kotlin {
    experimental.coroutines = Coroutines.ENABLE
}

repositories {
    mavenCentral()
    jcenter()
    maven { url = uri("https://dl.bintray.com/kotlin/ktor") }
    maven { url = uri("https://dl.bintray.com/kotlin/kotlin-eap") }
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
}

tasks.withType<KotlinCompile>().all {
    kotlinOptions.jvmTarget = "1.8"
}

application {
    mainClassName = "org.example.countdownmanagerapi.CountdownManagerApiKt"
}

dependencies {
    compile(kotlin("stdlib-jdk8"))
    compile("io.ktor:ktor-server-netty:$ktor_version")
    compile("io.ktor:ktor-jackson:$ktor_version")
    compile("io.ktor:ktor-auth-jwt:$ktor_version")
    compile("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:$jackson_version")
    compile("ch.qos.logback:logback-classic:1.2.3")
    compile("org.litote.kmongo:kmongo-coroutine:3.8.3")
    testCompile(group = "junit", name = "junit", version = "4.12")
}