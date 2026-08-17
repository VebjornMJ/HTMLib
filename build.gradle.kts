plugins {
    id("com.gradleup.shadow") version "9.6.1" apply false
}

allprojects {
    group = "dev.htmlib"
    version = "0.1.0"

    apply(plugin = "htmlib.repositories")
}

subprojects {
    apply(plugin = "htmlib.java-conventions")
}
