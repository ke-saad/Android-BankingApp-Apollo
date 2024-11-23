plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.apollographql.apollo").version("4.1.0")
}

android {
    namespace = "com.emsi.apollobankingapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.emsi.apollobankingapp"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.3"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    implementation("com.apollographql.apollo:apollo-runtime:4.1.0")
    implementation("com.apollographql.apollo:apollo-api:4.1.0")
    implementation("com.squareup.okhttp3:okhttp:4.11.0")
    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")
    implementation("androidx.activity:activity-compose:1.9.3")
    implementation("androidx.navigation:navigation-compose:2.8.4")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")
    implementation("androidx.compose.ui:ui:1.7.5")
    implementation("androidx.compose.material3:material3:1.3.1")
    testImplementation("junit:junit:4.13.2")
}

apollo {
    service("default") {
        packageName.set("com.emsi.apollobankingapp")
        srcDir("src/main/graphql/default")
        schemaFile.set(file("src/main/graphql/default/schema.graphqls"))
        sealedClassesForEnumsMatching.set(listOf(".*"))
        introspection {
            endpointUrl.set("http://localhost:8080/graphql")
            schemaFile.set(file("src/main/graphql/default/schema.graphqls"))
        }
    }
}
