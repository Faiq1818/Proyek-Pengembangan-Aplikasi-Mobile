import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeCompiler)
}

android {
    namespace = "com.example.mybawanggacha"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.example.mybawanggacha"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"

        val geminiApiKey = project.findProperty("GEMINI_API_KEY")?.toString()
            ?: System.getenv("GEMINI_API_KEY")
            ?: ""
        buildConfigField("String", "GEMINI_API_KEY", "\"$geminiApiKey\"")
    }

    buildFeatures {
        buildConfig = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}


abstract class SyncCommonComposeResourcesToAndroidAssets : DefaultTask() {
    @get:InputDirectory
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val inputDir: DirectoryProperty

    @get:Input
    abstract val packageName: Property<String>

    @get:OutputDirectory
    abstract val outputDir: DirectoryProperty

    @TaskAction
    fun sync() {
        val destination = outputDir.get()
            .asFile
            .resolve("composeResources/${packageName.get()}")
        val source = inputDir.get().asFile

        destination.deleteRecursively()
        destination.mkdirs()

        if (source.exists()) {
            source.copyRecursively(
                target = destination,
                overwrite = true
            )
        }
    }
}

androidComponents {
    onVariants { variant ->
        val capitalizedVariantName = variant.name.replaceFirstChar { char ->
            char.uppercaseChar()
        }
        val syncComposeResources = tasks.register<SyncCommonComposeResourcesToAndroidAssets>(
            "sync${capitalizedVariantName}CommonComposeResourcesToAndroidAssets"
        ) {
            inputDir.set(
                project(":composeApp")
                    .layout
                    .projectDirectory
                    .dir("src/commonMain/composeResources")
            )
            packageName.set("com.example.mybawanggacha.generated.resources")
        }

        variant.sources.assets?.addGeneratedSourceDirectory(
            syncComposeResources,
            SyncCommonComposeResourcesToAndroidAssets::outputDir
        )
    }
}

dependencies {
    implementation(project(":composeApp"))

    implementation(libs.androidx.activity.compose)
    implementation(libs.compose.uiToolingPreview)
    debugImplementation(libs.compose.uiTooling)
    implementation(libs.koin.core)
    implementation(libs.koin.androidx.compose)
}