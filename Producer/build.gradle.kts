dependencies {
    implementation(project(":Protocol"))
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
}

springBoot {
    mainClass.set("org.itmo.MainKt")
}

tasks.bootBuildImage {
    createdDate = "now"
    imageName = "lab2/${project.name.lowercase()}:${project.version}"
    tags.add("lab2/${project.name.lowercase()}:latest")
    environment = mapOf(
        "BP_JVM_VERSION" to "25"
    )
}