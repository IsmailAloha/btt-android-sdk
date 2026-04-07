package com.bluetriangle.bttplugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import kotlin.jvm.java

class BttPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val extension = project.extensions
            .create("btt", BttPluginExtension::class.java)

        // Wait for Android plugin to finish configuring
        project.afterEvaluate {
            // Hooks into assembleDebug, assembleRelease, assembleStaging etc.
            project.tasks.matching { task ->
                task.name.startsWith("assemble")
            }.configureEach { assembleTask ->
                assembleTask.doLast {
                    println("BTT: ${assembleTask.name} finished!")
                    println("BTT: composeNavigationInjectionEnabled = ${extension.composeNavigationInjectionEnabled.get()}")
                }
            }
        }
    }
}