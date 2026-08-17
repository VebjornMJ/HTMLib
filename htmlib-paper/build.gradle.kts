plugins {
    id("com.gradleup.shadow")
}

dependencies {
    implementation(project(":htmlib-api"))
    implementation(project(":htmlib-layout"))
    implementation(project(":htmlib-events"))
    implementation(project(":htmlib-navigation"))
    implementation(project(":htmlib-markup"))

    compileOnly("io.papermc.paper:paper-api:${project.property("paperApiVersion")}")
    compileOnly("me.clip:placeholderapi:2.11.6")
}

tasks.shadowJar {
    archiveClassifier.set("")
    // Our own htmlib-* modules are bundled as-is (no relocation): other plugins compile
    // against dev.htmlib.api / dev.htmlib.events directly and expect those exact runtime
    // packages once HTMLib is installed. paper-api / placeholderapi stay compileOnly since
    // the server (and PlaceholderAPI, if installed) provide them at runtime.
}

tasks.build {
    dependsOn(tasks.shadowJar)
}

tasks.processResources {
    val props = mapOf("version" to project.version)
    inputs.properties(props)
    filesMatching("plugin.yml") {
        expand(props)
    }
}
