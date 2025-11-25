dependencies {
    implementation(project(":Protocol"))

    implementation("edu.stanford.nlp:stanford-corenlp:4.5.10")
    implementation("edu.stanford.nlp:stanford-corenlp:4.5.10:models")
    implementation("javax.xml.bind:jaxb-api:2.3.1")
    implementation("com.sun.xml.bind:jaxb-core:2.3.0.1")
    implementation("com.sun.xml.bind:jaxb-impl:2.3.3")
    implementation("javax.activation:activation:1.1.1")
}

springBoot {
    mainClass.set("org.itmo.MainKt")
}

tasks.bootBuildImage {
    createdDate = "now"
    imageName = "lab2/${project.name.lowercase()}:${project.version}"
    tags.add("lab2/${project.name.lowercase()}:latest")
    environment = mapOf(
        "BP_JVM_VERSION" to "17"
    )
}