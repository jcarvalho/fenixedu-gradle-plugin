package org.fenixedu.gradle

import nl.javadude.gradle.plugins.license.LicensePlugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.plugins.MavenPlugin
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.publish.maven.plugins.MavenPublishPlugin
import org.gradle.api.tasks.bundling.Jar
import org.gradle.plugins.ide.eclipse.EclipsePlugin
import org.gradle.plugins.ide.idea.IdeaPlugin
import org.springframework.build.gradle.propdep.PropDepsMavenPlugin

class FenixEduPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {

        project.apply(plugin: MavenPlugin)
        project.apply(plugin: MavenPublishPlugin)
        project.apply(plugin: EclipsePlugin)
        project.apply(plugin: IdeaPlugin)
        project.apply(plugin: JavaPlugin)
        project.apply(plugin: PropDepsMavenPlugin)
        project.apply(plugin: LicensePlugin)

        project.repositories {
            mavenLocal()
            maven {
                url "https://repo.fenixedu.org/fenixedu-maven-repository"
            }
        }

        project.publishing {
            publications {
                mavenJava(MavenPublication) {
                    from project.components.java

                    pom.withXml() {
                        asNode().appendNode('description', project.description)
                        asNode().appendNode('name', project.name)
                        asNode().appendNode('organization')
                                    .appendNode('name', 'FenixEdu').parent()
                                    .appendNode('url', 'https://fenixedu.org')
                    }

                    artifact project.sourcesJar {
                        classifier "sources"
                    }
                    artifact project.javadocJar {
                        classifier "javadoc"
                    }
                }
            }
            repositories {
                maven {
                    credentials {
                        username project.properties['repoUsername']
                        password project.properties['repoPassword']
                    }
                    url "https://repo.fenixedu.org/fenixedu-releases"
                }
            }
        }

        project.dependencies {
            compile 'org.slf4j:slf4j-api:1.7.7'
            testCompile 'junit:junit:4.11'
            testCompile 'ch.qos.logback:logback-classic:1.1.2'
        }

        project.group = 'org.fenixedu'
        project.sourceCompatibility = '1.8'
        project.description = project.name

        project.afterEvaluate {
            project.jar {
                manifest.attributes["Build-Jdk"] =
                        "${System.getProperty("java.version")} (${System.getProperty("java.specification.vendor")})"
                manifest.attributes["Implementation-Title"] = project.description
                manifest.attributes["Implementation-Version"] = project.version
                manifest.attributes["Implementation-Vendor-Id"] = project.group
                manifest.attributes["Built-By"] = System.getProperty("user.name")
                manifest.attributes["Created-By"] = "Gradle ${project.gradle.gradleVersion}"

                // pom.properties compatibility
                manifest.attributes["version"] = project.version
                manifest.attributes["groupId"] = project.group
                manifest.attributes["artifactId"] = project.name
            }
        }

        project.compileJava {
            options.warnings = false
            options.encoding = "UTF-8"
        }

        project.javadoc {
            options.header = project.name
            options.addStringOption('Xdoclint:none', '-quiet')
            options.links("http://docs.oracle.com/javase/8/docs/api/")
            exclude(["**/*_Base.java", "**/ValueTypeSerializer.java", "**/CurrentBackEndId.java"])
        }

        project.task('sourcesJar', type: Jar, dependsOn: project.tasks.classes) {
            classifier = "sources"
            from project.sourceSets.main.allJava.srcDirs
        }

        project.task('javadocJar', type: Jar) {
            classifier = "javadoc"
            from project.javadoc
        }

    }

}
