package org.fenixedu.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project

class FenixEduFFPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {

        project.apply(plugin: FenixEduWebPlugin)
        project.apply(plugin: FFGradlePlugin)

        project.ext.ffVersion = "2.5.1"

        project.afterEvaluate {
            project.dependencies {
                compile "pt.ist:fenix-framework-core-api:${project.ext.ffVersion}"
            }
        }
    }
}
