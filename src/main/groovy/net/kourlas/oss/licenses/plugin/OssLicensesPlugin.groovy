/**
 * Copyright 2018 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.kourlas.oss.licenses.plugin

import com.android.build.api.artifact.SingleArtifact
import com.android.build.gradle.api.BaseVariant
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.slf4j.LoggerFactory

class OssLicensesPlugin implements Plugin<Project> {

    private static final logger = LoggerFactory.getLogger(DependencyTask.class)

    void apply(Project project) {
        def variantTolicenseTaskMap = new HashMap<String, LicensesTask>()
        project.androidComponents {
            onVariants(selector().all(), { variant ->
                def baseDir = new File(project.buildDir,
                        "generated/oss_licenses/${variant.name}")
                def dependenciesJson = new File(baseDir, "dependencies.json")

                def dependencyTask = project.tasks.register(
                        "${variant.name}OssDependencyTask",
                        DependencyTask.class) {
                    it.dependenciesJson.set(dependenciesJson)
                    it.libraryDependenciesReport.set(variant.artifacts.get(SingleArtifact.METADATA_LIBRARY_DEPENDENCIES_REPORT.INSTANCE))
                }.get()
                logger.debug("Created task ${dependencyTask.name}")

                def extendedDependenciesJson = new File(baseDir,
                        "dependencies_with_licenses.json")

                def licenseTask = project.tasks.register(
                        "${variant.name}OssLicensesTask",
                        LicensesTask.class) {
                    markNotCompatibleWithConfigurationCache(it)
                    it.dependenciesJson.set(dependencyTask.dependenciesJson)
                    it.extendedDependenciesJson = extendedDependenciesJson
                }.get()
                logger.debug("Created task ${licenseTask.name}")

                variantTolicenseTaskMap[variant.name] = licenseTask

                def cleanupTask = project.tasks.register(
                        "${variant.name}OssLicensesCleanUp",
                        LicensesCleanUpTask.class) {
                    it.dependenciesJson = dependenciesJson
                    it.extendedDependenciesJson = extendedDependenciesJson
                    it.dependencyDir = baseDir
                }.get()
                logger.debug("Created task ${cleanupTask.name}")

                project.tasks.findByName("clean").dependsOn(cleanupTask)
            })
        }
    }

    private static void markNotCompatibleWithConfigurationCache(Task it) {
        // Configuration cache method incubating in Gradle 7.4
        if (it.metaClass.respondsTo(it, "notCompatibleWithConfigurationCache", String)) {
            it.notCompatibleWithConfigurationCache(
                    "Requires Project instance to resolve POM files during " +
                            " task execution, but depends on another Task to " +
                            " create the artifact list. Without the list we " +
                            " cannot enumerate POM files during configuration."
            )
        }
    }
}
