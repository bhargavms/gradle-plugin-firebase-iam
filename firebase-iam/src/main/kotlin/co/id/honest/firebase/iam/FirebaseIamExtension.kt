package co.id.honest.firebase.iam

import org.gradle.api.Action

open class FirebaseIamExtension {
    private val _projects: MutableList<FirebaseProject> = mutableListOf()
    internal val projects: List<FirebaseProject> get() = _projects.toList()

    fun forProject(name: String, configuration: Action<FirebaseProject>) {
        _projects.add(
            FirebaseProject(name).also {
                configuration.execute(it)
            }
        )
    }
}
