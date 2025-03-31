package co.id.honest.firebase.iam

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.register

class FirebaseIamPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.extensions.create("firebaseIam", FirebaseIamExtension::class.java)
        // Register the main sync task
        project.tasks.register<SyncFirebasePermissionsTask>("syncFirebasePermissions") {
            group = "firebase"
            description = "Synchronizes Firebase permissions based on configuration"
        }

        project.afterEvaluate {
            // Make sure Google Services plugin is applied
            if (!project.plugins.hasPlugin("com.google.gms.google-services")) {
                project.logger.warn("Google Services plugin is not applied.")
            }
        }
    }
}
