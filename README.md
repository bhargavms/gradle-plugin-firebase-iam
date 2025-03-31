# Firebase IAM Gradle Plugin

A Gradle plugin for managing Firebase project IAM permissions, written in Kotlin.

## Features

- Manage editor and viewer permissions for multiple Firebase projects
- Simple DSL for defining permissions
- Automatically applies permissions using Google Cloud Resource Manager API
- Written in Kotlin with full Kotlin DSL support

## Prerequisites

- `com.google.gms.google-services` applied.
- Appropriate permissions to modify IAM policies on the target Firebase projects
- Authentication set up service account credentials

## Installation

Add the plugin to your build script:

### Kotlin DSL (build.gradle.kts)

```kotlin
plugins {
    id("com.firebase.iam") version "1.0.0"
}
```

### Groovy DSL (build.gradle)

```groovy
plugins {
    id 'com.firebase.iam' version '1.0.0'
}
```

## Usage

Configure the plugin in your build file:

### Kotlin DSL (build.gradle.kts)

```kotlin
configure<com.firebase.iam.FirebaseIamExtension> {
    project("my-project-1") {
        credentialFile = project.file("firebase-project-1.json")
        editors = listOf("user1@example.com", "user2@example.com")
        viewers = listOf("user3@example.com", "user4@example.com")
    }

    project("my-project-2") {
        credentialFile = project.file("firebase-project-2.json")
        editors = listOf("user5@example.com")
        viewers = listOf("user6@example.com", "user7@example.com")
    }
}
```

### Groovy DSL (build.gradle)

```groovy
firebaseIam {
    project("my-project-1") {
        it.credentialFile = project.file("firebase-project-1.json")
        it.editors = ["user1@example.com", "user2@example.com"]
        it.viewers = ["user3@example.com", "user4@example.com"]
    }

    project("my-project-2") {
        it.credentialFile = project.file("firebase-project-2.json")
        it.editors = ["user5@example.com"]
        it.viewers = ["user6@example.com", "user7@example.com"]
    }
}
```

Apply the IAM permissions by running:

```
./gradlew syncFirebasePermissions
```
