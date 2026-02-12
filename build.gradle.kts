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

val ktorVersion = "2.3.7"

dependencies {

    // Ktor Server
    implementation("io.ktor:ktor-server-core-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-netty-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-status-pages-jvm:$ktorVersion")

    // Ktor Client
    implementation("io.ktor:ktor-client-core-jvm:$ktorVersion")
    implementation("io.ktor:ktor-client-cio-jvm:$ktorVersion")

    // Logging
    implementation("ch.qos.logback:logback-classic:1.4.14")

    implementation("com.microsoft.sqlserver:mssql-jdbc:12.6.1.jre11")
    implementation("com.zaxxer:HikariCP:5.1.0")
}
