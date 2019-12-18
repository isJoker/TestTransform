package com.jokerwan.demo.plugin

import com.android.build.gradle.AppExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

public class JokerWanPlugin implements Plugin<Project> {
    void apply(Project project) {
        AppExtension appExtension = project.extensions.findByType(AppExtension.class)
        appExtension.registerTransform(new JokerWanTransformNew(project))
    }
}