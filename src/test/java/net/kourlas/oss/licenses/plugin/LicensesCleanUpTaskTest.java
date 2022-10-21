/**
 * Copyright 2018 Google LLC
 * Modifications copyright (C) 2022 Michael Kourlas
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package net.kourlas.oss.licenses.plugin;

import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertFalse;

/** Tests for {@link LicensesCleanUpTask} */
@RunWith(JUnit4.class)
public class LicensesCleanUpTaskTest {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void testAction() throws IOException {
        File testDir = temporaryFolder.newFolder();

        File dependencyDir = new File(testDir, "dependency");
        dependencyDir.mkdir();

        File dependencyFile = new File(dependencyDir, "dependency.json");

        File extendedDependenciesJson = new File(testDir, "dependencies-with-licenses.json");

        Project project = ProjectBuilder.builder().withProjectDir(testDir).build();
        LicensesCleanUpTask task =
                project.getTasks().create("licensesCleanUp", LicensesCleanUpTask.class);
        task.dependencyDir = dependencyDir;
        task.dependenciesJson = dependencyFile;
        task.extendedDependenciesJson = extendedDependenciesJson;

        task.action();
        assertFalse(task.dependenciesJson.exists());
        assertFalse(task.dependencyDir.exists());
    }
}
