plugins {
    kotlin("jvm") version "2.0.0"
    kotlin("plugin.spring") version "2.0.0"
    id("org.springframework.boot") version "3.4.2" apply false
    id("io.spring.dependency-management") version "1.1.7"
}

allprojects {
    group = "org.itmo"
    version = "1.0-SNAPSHOT"

    repositories {
        mavenCentral()
    }
}


subprojects {
    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "org.jetbrains.kotlin.plugin.spring")
    apply(plugin = "io.spring.dependency-management")
    apply(plugin = "org.springframework.boot")

    dependencies {
        implementation("org.jetbrains.kotlin:kotlin-stdlib")
        implementation("org.jetbrains.kotlin:kotlin-reflect")

        implementation("org.springframework.boot:spring-boot-starter-amqp")
        implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    }

    tasks.test {
        useJUnitPlatform()
    }
}