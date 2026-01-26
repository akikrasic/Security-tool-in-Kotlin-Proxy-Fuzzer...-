import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "2.2.0"
}

group = "srb.aki"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    google()
    maven {
        url = uri("https://oss.sonatype.org/content/repositories/releases/")
    }
}

dependencies {
    // https://mvnrepository.com/artifact/com.squareup.okhttp3/okhttp
    //prckao sam ove biblioteke bc verzije i ovu commons compress a dirao sam i 33. liniju da vdimo sto ovo ovde radi a tamo ne radi
    implementation("org.bouncycastle:bcpkix-fips:2.0.8")
    implementation("org.bouncycastle:bctls-fips:1.0.19")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation("com.nixxcode.jvmbrotli:jvmbrotli:0.2.0")
    implementation("com.nixxcode.jvmbrotli:jvmbrotli-linux-x86-amd64:0.2.0")
    implementation("com.nixxcode.jvmbrotli:jvmbrotli-parent:0.2.0")
    implementation("org.apache.commons:commons-compress:1.28.0")
    implementation(platform("com.squareup.okhttp3:okhttp-bom:5.3.0"))
    // define any required OkHttp artifacts without version
    implementation("com.squareup.okhttp3:okhttp")


    testImplementation(kotlin("test-junit5"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.6.0")
    implementation(kotlin("stdlib-jdk8")) //zbog ovo izgleda radi ovo
    testImplementation ("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
}

kotlin {
    jvmToolchain(25)
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}
kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_24) // Or your desired JVM version
    }
}
java {
    targetCompatibility = JavaVersion.VERSION_24 // Or your desired JVM version
    sourceCompatibility = JavaVersion.VERSION_24 // Or your desired JVM version
}


