plugins {
    application
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

buildscript {
    repositories {
        mavenCentral()
    }

    dependencies {
        classpath("com.guardsquare:proguard-gradle:7.3.0")
    }
}

allprojects {
    apply(plugin = "application")
    apply(plugin = "com.github.johnrengelman.shadow")

    dependencies {
        val slf4jVersion = "1.7.36"
        val lombokVersion = "1.18.24"

        implementation(group = "org.slf4j", name = "slf4j-api", version = slf4jVersion)
        annotationProcessor(group = "org.projectlombok", name = "lombok", version = lombokVersion)
        compileOnly(group = "javax.annotation", name = "javax.annotation-api", version = "1.3.2")
        compileOnly(group = "org.projectlombok", name = "lombok", version = lombokVersion)
        implementation(group = "com.squareup.okhttp3", name = "okhttp", version = "4.9.1")
        implementation(group = "ch.qos.logback", name = "logback-classic", version = "1.2.9")
        compileOnly(group = "org.projectlombok", name = "lombok", version = lombokVersion)
        // https://mvnrepository.com/artifact/com.google.code.findbugs/jsr305
        implementation("com.google.code.findbugs:jsr305:3.0.2")

        implementation(group = "com.google.guava", name = "guava", version = "30.1.1-jre") {
            exclude(group = "com.google.code.findbugs", module = "jsr305")
            exclude(group = "com.google.errorprone", module = "error_prone_annotations")
            exclude(group = "com.google.j2objc", module = "j2objc-annotations")
            exclude(group = "org.codehaus.mojo", module = "animal-sniffer-annotations")
        }

        implementation("org.apache.commons:commons-text:1.10.0")

        implementation("javax.inject:javax.inject:1")
        implementation("org.apache.commons:commons-lang3:3.13.0")

        implementation(group = "commons-io", name = "commons-io", version = "2.8.0")
    }

    java {
        setSourceCompatibility(JavaVersion.VERSION_11.toString())
        setTargetCompatibility(JavaVersion.VERSION_11.toString())
    }

}

tasks.withType<JavaCompile>().configureEach {
    options.isWarnings = false
    options.isDeprecation = false
    options.isIncremental = true
}

tasks {
    jar {
        destinationDirectory.set(file("${rootProject.buildDir}\\"))
    }
}