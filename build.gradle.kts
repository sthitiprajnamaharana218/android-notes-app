// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    ext {
        compose_version = '1.5.4'
    }
    dependencies {
        classpath "com.google.dagger:hilt-android-gradle-plugin:2.44"
    }
}

plugins {
    id 'com.android.application' version '8.1.1' apply false
    id 'com.android.library' version '8.1.1' apply false
    id 'org.jetbrains.kotlin.android' version '1.8.20' apply false
    id 'com.google.dagger.hilt.android' version '2.44' apply false
}
