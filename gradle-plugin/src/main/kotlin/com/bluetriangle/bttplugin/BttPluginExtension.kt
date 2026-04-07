package com.bluetriangle.bttplugin

import org.gradle.api.Project
import org.gradle.api.provider.Property
import javax.inject.Inject

open class BttPluginExtension @Inject constructor(project: Project) {

    val composeNavigationInjectionEnabled: Property<Boolean> = project.objects
        .property(Boolean::class.java)
        .convention(true)

}
