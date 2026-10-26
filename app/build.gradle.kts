plugins { id("com.android.application") }

android {
    namespace = "com.example.examencontactos"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.examencontactos"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    // Mantener alineadas las bibliotecas Kotlin transitivas para evitar clases duplicadas.
    implementation(platform("org.jetbrains.kotlin:kotlin-bom:1.8.22"))
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("androidx.activity:activity:1.9.3")
    implementation("androidx.core:core:1.15.0")
}
