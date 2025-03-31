plugins {
    `kotlin-dsl`
    alias(libs.plugins.kotlin.lint.jlleitschuh)
    id("com.gradle.plugin-publish") version "1.3.1"
}
group = "dev.mogra"
version = "1.0.0"
configure<org.jlleitschuh.gradle.ktlint.KtlintExtension> {
    version.set("0.43.2")
    android.set(true)
    ignoreFailures.set(false)
}

gradlePlugin {
    website.set("https://github.com/bhargavms/gradle-plugin-firebase-iam")
    vcsUrl.set("https://github.com/bhargavms/gradle-plugin-firebase-iam")
    plugins {
        create("firebaseUserIAMPlugin") {
            id = "dev.mogra.firebase.iam"
            implementationClass = "dev.mogra.firebase.iam.FirebaseIAMPlugin"
            displayName = "Firebase User IAM Gradle Plugin"
            description = "Manages firebaser user access and permissions based on a simple configuration"
            tags.set(listOf("firebase", "firebase permissions", "firebase user", "firebase iam", "iac-iam", "firebase access control"))
        }
    }
}

dependencies {
    implementation(gradleApi())

    // Google APIs for IAM management
    implementation(libs.google.api.client)
    implementation(libs.google.api.services.cloudresourcemanager)
    implementation(libs.google.auth.library.oauth2.http)
    implementation(libs.plugin.android.gradle)
}
