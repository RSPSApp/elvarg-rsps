description = "Elvarg Plugins"


val lib = rootProject.project.libs
dependencies {
    implementation(project(":game"))
    implementation(kotlin("script-runtime"))
}

tasks.named<Jar>("jar") {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}
