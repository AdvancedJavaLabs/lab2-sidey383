plugins {
    id("org.springframework.boot")
}

dependencies {
    implementation(project(":Protocol"))

    implementation("org.springframework.boot:spring-boot-starter-logging")
    implementation("org.apache.activemq:activemq-broker:6.2.0")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
}

springBoot {
    mainClass.set("org.itmo.MainKt")
}