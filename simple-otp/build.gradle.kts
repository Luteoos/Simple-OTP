plugins {
    id("maven-publish")
    kotlin("multiplatform")
    kotlin("native.cocoapods")
    id("com.android.library")
}

/**
 * Parameters taken from gradle.properties
 *
 * Before running `gradlew publishToMavenLocal` verify gradle.properties
 */
val VERSION: String by project
val GROUP: String by project
val ARTIFACT: String by project
val JITPACK: String by project
val isRelease: String by project

group = GROUP
version = "$VERSION${
    if(isRelease.toBoolean())
        ""
    else
        "-SNAPSHOT"
}"

logger.info("Simple-OTP group=$GROUP artifactId=$ARTIFACT version=${project.version} buildOnJitpack=$JITPACK")

apply(from = "../ktlint.gradle")

@OptIn(org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi::class)
kotlin {
    targetHierarchy.default()


    android {
        compilations.all {
            kotlinOptions {
                jvmTarget = "1.8"
            }
        }
        publishLibraryVariants("release")
    }
    iosX64()
    iosArm64()
    iosSimulatorArm64()
//    ios()
    jvm()
    watchos()
    macosArm64()
    macosX64()

//    val publicationsFromMainHost =
//        listOf(jvm()).map { it.name } + "kotlinMultiplatform"
//    publishing {
//        publications {
//            matching { it.name in publicationsFromMainHost }.all {
//                val targetPublication = this@all
//                tasks.withType<AbstractPublishToMaven>()
//                    .matching { it.publication == targetPublication }
//                    .configureEach { onlyIf { findProperty("isMainHost") == "true" } }
//            }
//        }
//    }

    cocoapods {
        summary = "Simple-OTP KMP library"
        homepage = "https://luteoos.dev"
        version = "1.0"
        ios.deploymentTarget = "14.1"
        authors = "Mateusz Lutecki"
        framework {
            baseName = "simple-otp"
        }
    }
    
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.0")
                implementation("com.squareup.okio:okio:3.7.0")
                implementation("io.matthewnelson.encoding:base32:2.0.0")

                val cryptoVersion = "0.3.0"
                // HmacMD5
                implementation("org.kotlincrypto.macs:hmac-md:${cryptoVersion}")
                // HmacSHA1
                implementation("org.kotlincrypto.macs:hmac-sha1:${cryptoVersion}")
                // HmacSHA224, HmacSHA256, HmacSHA384, HmacSHA512
                // HmacSHA512/t, HmacSHA512/224, HmacSHA512/256
                implementation("org.kotlincrypto.macs:hmac-sha2:${cryptoVersion}")

            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val androidMain by getting {
            dependsOn(commonMain)
        }
        val iosMain by getting {
            dependsOn(commonMain)
        }
    }
}

android {
    namespace = "dev.luteoos.simpleotp"
    compileSdk = 33
    defaultConfig {
        minSdk = 25
    }
}

publishing {
    publications {
        register<MavenPublication>("release") {
            groupId = GROUP
            artifactId = ARTIFACT

            afterEvaluate {
                from(components["release"])
            }
            pom {
                name.set(ARTIFACT)
                description.set("Simple OTP for Kotlin Multiplatform")
                licenses {
                    license {
                        name.set("MIT License")
                        url.set("http://www.opensource.org/licenses/mit-license.php")
                    }
                }
                developers {
                    developer {
                        id.set("Luteoos")
                        name.set("Mateusz Lutecki")
                        email.set("mateusz.lutecki.it@gmail.com")
                        url.set("http://luteoos.dev")
                    }
                }
            }
        }
    }
}

// Workaround for https://youtrack.jetbrains.com/issue/KT-51970
afterEvaluate {
    afterEvaluate {
        tasks.configureEach {
            if (
                name.startsWith("compile")
                && name.endsWith("KotlinMetadata")
            ) {
                println("disabling ${this}:$name")
                enabled = false
            }
        }
    }
}