package co.id.honest.firebase.iam

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.cloudresourcemanager.CloudResourceManager
import com.google.api.services.cloudresourcemanager.model.Binding
import com.google.api.services.cloudresourcemanager.model.GetIamPolicyRequest
import com.google.api.services.cloudresourcemanager.model.Policy
import com.google.api.services.cloudresourcemanager.model.SetIamPolicyRequest
import com.google.auth.http.HttpCredentialsAdapter
import com.google.auth.oauth2.GoogleCredentials
import java.io.File
import java.io.FileInputStream
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

open class SyncFirebasePermissionsTask : DefaultTask() {
    companion object {
        private val ROLE_MAPPINGS = mapOf(
            "editors" to "roles/firebase.developAdmin",
            "viewers" to "roles/firebase.viewer"
        )
    }

    @TaskAction
    fun syncPermissions() {
        val firebaseExt = project.extensions.getByType(FirebaseIamExtension::class.java)

        // Get project ID from configuration or google-services.json
        firebaseExt.projects.forEach {
            val projectId = it.name
            logger.lifecycle("Syncing Firebase permissions for project: $projectId")

            // Get service account key path
            val keyPath = it.credentialFile ?: project.file("firebase-$projectId.json")
            if (!keyPath.exists()) {
                throw IllegalStateException("Service account key not found at: $keyPath. Please create this file.")
            }

            // Initialize the resource manager
            val resourceManager = initResourceManager(keyPath)

            // Get current IAM policy
            val currentPolicy = getCurrentPolicy(resourceManager, projectId)
            val updatedPolicy = createUpdatedPolicy(currentPolicy, it)

            // Update the policy
            updatePolicy(resourceManager, projectId, updatedPolicy)

            logger.lifecycle("Firebase permissions synchronized successfully!")
        }
    }

    private fun initResourceManager(keyFile: File): CloudResourceManager {
        val credentials = GoogleCredentials
            .fromStream(FileInputStream(keyFile))
            .createScoped("https://www.googleapis.com/auth/cloud-platform")

        val httpTransport = GoogleNetHttpTransport.newTrustedTransport()
        val jsonFactory = GsonFactory.getDefaultInstance()

        return CloudResourceManager.Builder(
            httpTransport, jsonFactory, HttpCredentialsAdapter(credentials)
        )
            .setApplicationName("firebase-gradle-management")
            .build()
    }

    private fun getCurrentPolicy(resourceManager: CloudResourceManager, projectId: String): Policy {
        val getRequest = resourceManager.projects().getIamPolicy(projectId, GetIamPolicyRequest())
        return getRequest.execute()
    }

    private fun createUpdatedPolicy(currentPolicy: Policy, firebaseProject: FirebaseProject): Policy {
        // Clone the current bindings to avoid modifying them directly
        val existingBindings = currentPolicy.bindings ?: listOf()
        val newBindings = mutableListOf<Binding>()

        // Create a map to easily lookup bindings by role
        val bindingsByRole = existingBindings.associateBy { it.role }

        // Process each role type (admins, editors, viewers)
        ROLE_MAPPINGS.forEach { (roleType, googleRole) ->
            val users = when (roleType) {
                "editors" -> firebaseProject.editors
                "viewers" -> firebaseProject.viewers
                else -> listOf()
            }

            val members = users.map { "user:$it" }

            // If we have users for this role, create or update the binding
            if (members.isNotEmpty()) {
                val binding = bindingsByRole[googleRole]
                    ?: Binding().setRole(googleRole).setMembers(listOf())

                // Filter out any service accounts or other non-user members we want to preserve
                val existingMembers = binding.members
                    ?.filter { !it.startsWith("user:") }
                    ?: listOf()

                // Combine preserved members with our configured user members
                binding.members = existingMembers + members
                newBindings.add(binding)
            }
        }

        // Add back any bindings for roles we're not managing
        existingBindings.forEach { binding ->
            if (!ROLE_MAPPINGS.containsValue(binding.role)) {
                newBindings.add(binding)
            }
        }

        // Update the policy with our new bindings
        currentPolicy.bindings = newBindings
        return currentPolicy
    }

    private fun updatePolicy(
        resourceManager: CloudResourceManager,
        projectId: String,
        policy: Policy
    ) {
        val setRequest = resourceManager.projects().setIamPolicy(
            projectId, SetIamPolicyRequest().setPolicy(policy)
        )
        setRequest.execute()
    }
}
