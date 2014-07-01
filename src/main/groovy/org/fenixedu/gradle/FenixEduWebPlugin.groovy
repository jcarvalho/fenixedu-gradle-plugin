package org.fenixedu.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.Copy

class FenixEduWebPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        project.apply(plugin: FenixEduPlugin)

        project.ext.servletApiVersion = "3.0.1"

        project.afterEvaluate {
            project.dependencies {
                provided "javax.servlet:javax.servlet-api:${project.ext.servletApiVersion}"
            }

            if (project.tasks.findByName('war')) {
                project.task('explodedWar', type: Copy) {
                    into "${project.buildDir}/exploded"
                    with project.tasks.war
                }
            } else {
                project.task('copyWebappResources', type: Copy) {
                    from 'src/main/webapp'
                    into "${project.sourceSets.main.output.resourcesDir}/META-INF/resources"
                }
                project.tasks.processResources.dependsOn('copyWebappResources')
            }
        }
    }
}
