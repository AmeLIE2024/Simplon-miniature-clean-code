plugins {
    // Apply the application plugin to add support for building a CLI application in Java.
    application
}

repositories {
    mavenCentral()
}

dependencies {
    var tomcatVersion = "11.0.18"
    implementation ("org.apache.tomcat.embed:tomcat-embed-core:${tomcatVersion}")
    implementation ("org.apache.tomcat.embed:tomcat-embed-jasper:${tomcatVersion}")
    // This dependency is used by the application.
    implementation("com.google.guava:guava:33.0.0-jre")
}


// Apply a specific Java toolchain to ease working on different environments.
java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

application {
    mainClass = "org.miniature.App"
}


tasks.named<Test>("test") {
    // Use JUnit Platform for unit tests.
    useJUnitPlatform()
}