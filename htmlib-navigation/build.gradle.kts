dependencies {
    api(project(":htmlib-api"))
    api(project(":htmlib-events"))
    compileOnlyApi("io.papermc.paper:paper-api:${project.property("paperApiVersion")}")
}
