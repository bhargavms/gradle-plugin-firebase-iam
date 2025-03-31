package co.id.honest.firebase.iam

import java.io.File

open class FirebaseProject(val name: String) {
    var editors: List<String> = emptyList()
    var viewers: List<String> = emptyList()
    var credentialFile: File? = null
}
