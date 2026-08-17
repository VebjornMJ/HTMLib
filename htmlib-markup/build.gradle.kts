dependencies {
    api(project(":htmlib-api"))
    compileOnlyApi("io.papermc.paper:paper-api:${project.property("paperApiVersion")}")
}
