dependencies {
    implementation(project(":Protocol"))
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