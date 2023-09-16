group = "com.runescape"
version = "0.0.1"

dependencies {
    implementation(project(":api"))
    implementation("org.nanohttpd:nanohttpd:2.3.1")
    implementation("com.beust:klaxon:5.5")
}

tasks {
    register<JavaExec>("Run-Vanilla") {
        group = "RspsApp"
        description = "Run Client in Vanilla State"
        classpath = project.sourceSets.main.get().runtimeClasspath
        mainClass.set("com.runescape.Application")
    }
}