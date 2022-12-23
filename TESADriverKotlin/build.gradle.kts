
plugins {
    id("java-library")
    id("org.jetbrains.kotlin.jvm")
    id("maven-publish")
//    id("kotlin-parcelize")
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

dependencies {
//    implementation(kotlin("stdlib-jdk8"))
//    // ViewModel
//    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.5.1")
//    // ViewModel utilities for Compose
//    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.5.1")
//
//    implementation("androidx.lifecycle:lifecycle-extensions:2.2.0")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:1.6.4")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")

    // serialization/deserialization/encoding/decoding
    implementation("com.google.code.gson:gson:2.10")

    // websocket
    implementation("com.squareup.okhttp3:okhttp:5.0.0-alpha.10")

    // multi-threading
    implementation("io.ktor:ktor-client-core:2.1.3")

    // logging
    implementation("io.github.microutils:kotlin-logging-jvm:3.0.4")
    implementation("ch.qos.logback:logback-classic:1.4.4")
    implementation("org.apache.logging.log4j:log4j-core:2.19.0")
    implementation("org.apache.logging.log4j:log4j-slf4j-impl:2.19.0")


}



afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("maven") {

                from(components["java"])

                groupId = "com.github.wsdev"
                artifactId = "TESADriver-Kotlin"
                version = "0.0.8"

            }
        }
    }
}
