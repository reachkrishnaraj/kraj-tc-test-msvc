import jetbrains.buildServer.configs.kotlin.v2018_1.*
import jetbrains.buildServer.configs.kotlin.v2018_1.buildFeatures.commitStatusPublisher
import jetbrains.buildServer.configs.kotlin.v2018_1.buildFeatures.dockerSupport
import jetbrains.buildServer.configs.kotlin.v2018_1.buildFeatures.vcsLabeling
import jetbrains.buildServer.configs.kotlin.v2018_1.buildSteps.gradle
import jetbrains.buildServer.configs.kotlin.v2018_1.buildSteps.script
import jetbrains.buildServer.configs.kotlin.v2018_1.triggers.vcs

/*
The settings script is an entry point for defining a TeamCity
project hierarchy. The script should contain a single call to the
project() function with a Project instance or an init function as
an argument.

VcsRoots, BuildTypes, Templates, and subprojects can be
registered inside the project using the vcsRoot(), buildType(),
template(), and subProject() methods respectively.

To debug settings scripts in command-line, run the

    mvnDebug org.jetbrains.teamcity:teamcity-configs-maven-plugin:generate

command and attach your debugger to the port 8000.

To debug in IntelliJ Idea, open the 'Maven Projects' tool window (View
-> Tool Windows -> Maven Projects), find the generate task node
(Plugins -> teamcity-configs -> teamcity-configs:generate), the
'Debug' option is available in the context menu for the task.
*/

version = "2018.1"

project {

    buildType(BuildTest)
}

object BuildTest : BuildType({
    name = "build & test"

    params {
        param("image_name", "test-service-name-msvc")
        param("test-service-name-msvc_version", "UNSET")
        param("docker_image", "UNSET")
    }

    vcs {
        root(DslContext.settingsRoot)
    }

    steps {
        gradle {
            tasks = "clean build buildDocker"
            buildFile = ""
            gradleParams = """
                -PbuildNumber=%build.number%
                -Pbranch=%teamcity.build.branch%
                -PisDefault=%teamcity.build.branch.is_default%
                -PartifactoryUsername=%artifactoryUsername%
                -PartifactoryAuthToken=%artifactoryAuthToken%
            """.trimIndent()
            coverageEngine = idea {
                includeClasses = "com.turo.*"
            }
            param("org.jfrog.artifactory.selectedDeployableServer.useM2CompatiblePatterns", "true")
            param("org.jfrog.artifactory.selectedDeployableServer.publishBuildInfo", "true")
            param("org.jfrog.artifactory.selectedDeployableServer.defaultModuleVersionConfiguration", "GLOBAL")
            param("org.jfrog.artifactory.selectedDeployableServer.urlId", "0")
            param("org.jfrog.artifactory.selectedDeployableServer.envVarsExcludePatterns", "*password*,*secret*")
            param("org.jfrog.artifactory.selectedDeployableServer.resolvingRepo", "turo-release-private")
            param("org.jfrog.artifactory.selectedDeployableServer.publishMavenDescriptors", "true")
            param("org.jfrog.artifactory.selectedDeployableServer.publishIvyDescriptors", "true")
            param("org.jfrog.artifactory.selectedDeployableServer.deployArtifacts", "true")
            param("org.jfrog.artifactory.selectedDeployableServer.activateGradleIntegration", "true")
            param("org.jfrog.artifactory.selectedDeployableServer.targetRepo", "libs-release-local")
            param("org.jfrog.artifactory.selectedDeployableServer.deployIncludePatterns", "**/test-service-name-client/**,**/test-service-name-common/**")
        }
        script {
            name = "Set docker_image"
            scriptContent = """
                #!/bin/bash -xe

                export IMAGE=turo/%image_name%:%test-service-name-msvc_version%
                echo "##teamcity[setParameter name='docker_image' value='${'$'}IMAGE']"
            """.trimIndent()
            param("org.jfrog.artifactory.selectedDeployableServer.downloadSpecSource", "Job configuration")
            param("org.jfrog.artifactory.selectedDeployableServer.useSpecs", "false")
            param("org.jfrog.artifactory.selectedDeployableServer.uploadSpecSource", "Job configuration")
        }
        script {
            name = "push docker image & local_dev image"
            scriptContent = """
                #!/bin/bash -xe

                export IMAGE=%docker_image%
                export LATEST_LOCAL_DEV_IMAGE=turo/%image_name%:latest_local-dev
                export VERSIONED_LOCAL_DEV_IMAGE=%docker_image%_local-dev

                docker tag ${'$'}IMAGE ${'$'}LATEST_LOCAL_DEV_IMAGE
                docker tag ${'$'}IMAGE ${'$'}VERSIONED_LOCAL_DEV_IMAGE

                docker push ${'$'}IMAGE
                docker push ${'$'}LATEST_LOCAL_DEV_IMAGE
                docker push ${'$'}VERSIONED_LOCAL_DEV_IMAGE
            """.trimIndent()
            param("org.jfrog.artifactory.selectedDeployableServer.downloadSpecSource", "Job configuration")
            param("org.jfrog.artifactory.selectedDeployableServer.useSpecs", "false")
            param("org.jfrog.artifactory.selectedDeployableServer.uploadSpecSource", "Job configuration")
        }
    }

    triggers {
        vcs {
        }
    }

    features {
        dockerSupport {
            loginToRegistry = on {
                dockerRegistryId = "PROJECT_EXT_61"
            }
        }
        commitStatusPublisher {
            vcsRootExtId = "${DslContext.settingsRoot.id}"
            publisher = github {
                githubUrl = "https://api.github.com"
                authType = personalToken {
                    token = "credentialsJSON:013b0508-df37-4bd4-b458-0cf9f2e65fa1"
                }
            }
            param("github_oauth_provider_id", "PROJECT_EXT_1")
        }
        vcsLabeling {
            vcsRootId = "${DslContext.settingsRoot.id}"
            labelingPattern = "%test-service-name-msvc_version%"
            successfulOnly = true
        }
        feature {
            type = "perfmon"
        }
    }
})
