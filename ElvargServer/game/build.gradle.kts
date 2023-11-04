plugins {
    application
}

application {
    apply(plugin = "maven-publish")
    mainClass.set("com.elvarg.Server")
}

val lib = rootProject.project.libs
dependencies {
    with(lib) {
        implementation(commons)
        implementation(commons.lang)
        implementation(classgraph)
        implementation(slf4j.api)
        implementation(okhttp3)
        implementation(password4J)
        implementation(dynamodb)
        implementation(joda)
        implementation(dynamodb.enhanced)
        implementation(netty.all)
    }
    runtimeOnly(project(":plugin"))
}

tasks.named<Jar>("jar") {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}
tasks.withType<ProcessResources> {
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}

tasks {

    build {
        this.finalizedBy(project(":plugin").tasks.getByName("build"))
    }

    buildNeeded {
        this.finalizedBy(project(":plugin").tasks.getByName("buildNeeded"))
    }

    compileKotlin {
        this.finalizedBy(project(":plugin").tasks.getByName("build"))
    }

}

