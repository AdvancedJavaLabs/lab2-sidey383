plugins {
    id("org.springframework.boot")
}

dependencies {
    implementation(project(":Protocol"))
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-amqp")

    implementation("org.springframework.boot:spring-boot-starter-logging")
    implementation("org.apache.activemq:activemq-broker:6.2.0")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    implementation("org.apache.opennlp:opennlp-tools:2.3.0")

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