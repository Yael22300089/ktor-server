plugins {
    kotlin("jvm") version "1.9.22"
    application
}

group = "com.tuempresa"
version = "0.0.1"

application {
    mainClass.set("com.tuempresa.app.ApplicationKt")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.ktor:ktor-server-core-jvm:2.3.7")
    implementation("io.ktor:ktor-server-netty-jvm:2.3.7")
    implementation("io.ktor:ktor-server-status-pages-jvm:2.3.7")
}


//
//plugins {
//    kotlin("jvm") version "1.9.22"
//    application
//}
//
//application {
//    mainClass.set("com.tuempresa.app.ApplicationKt")
//}
//
//dependencies {
//    implementation("io.ktor:ktor-server-core-jvm:2.3.7")
//    implementation("io.ktor:ktor-server-netty-jvm:2.3.7")
//    implementation("io.ktor:ktor-server-status-pages-jvm:2.3.7")
//
//    // Logger (evita warnings)
//    implementation("ch.qos.logback:logback-classic:1.4.14")
//}

