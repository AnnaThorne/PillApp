// Top-level build file where you can add configuration options common to all sub-projects/modules.

plugins {
    id("com.android.application") version "8.1.4" apply false
    id("org.jetbrains.kotlin.android") version "1.8.10" apply false
    id("org.jetbrains.kotlin.jvm") version "1.8.10" apply false
}

buildscript {
    val properties = java.util.Properties().apply {
        load(rootProject.file("local.properties").reader())
    }
    extra.apply{
        set("keystorePassword", properties.getProperty("keystore.password"))
        set("keyPassword", properties.getProperty("key.password"))
        set("keyAlias", properties.getProperty("key.alias"))
        set("keystorePath", properties.getProperty("keystore.path"))
    }
}