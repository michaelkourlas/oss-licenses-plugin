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

import static org.gradle.internal.impldep.org.testng.Assert.fail;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/** Tests for {@link LicensesTask} */
@RunWith(JUnit4.class)
public class LicensesTaskTest {

  private static final Charset UTF_8 = StandardCharsets.UTF_8;
  private static final String BASE_DIR = "src/test/resources";
  private static final String LINE_BREAK = System.getProperty("line.separator");
  @Rule
  public TemporaryFolder temporaryFolder = new TemporaryFolder();
  private Project project;
  private LicensesTask licensesTask;

  @Before
  public void setUp() throws IOException {
    File outputDir = temporaryFolder.newFolder();
    File outputExtendedDependencies = new File(outputDir, "testExtendedDependencies");

    project = ProjectBuilder.builder().withProjectDir(new File(BASE_DIR)).build();
    licensesTask = project.getTasks().create("generateLicenses", LicensesTask.class);

    licensesTask.setExtendedDependenciesJson(outputExtendedDependencies);
  }

  @Test
  public void testInitExtendedDependencies() throws IOException {
    licensesTask.initExtendedDependenciesJson();

    assertTrue(licensesTask.getExtendedDependenciesJson().exists());
    assertEquals(0, Files.size(licensesTask.getExtendedDependenciesJson().toPath()));
  }

  @Test
  public void testIsGranularVersion_True() {
    String versionTrue = "14.6.0";
    assertTrue(LicensesTask.isGranularVersion(versionTrue));
  }

  @Test
  public void testIsGranularVersion_False() {
    String versionFalse = "11.4.0";
    assertFalse(LicensesTask.isGranularVersion(versionFalse));
  }

  @Test
  public void testAddLicensesFromPom() throws IOException, URISyntaxException {
    File deps1 = getResourceFile("dependencies/groupA/deps1.pom");
    String name1 = "deps1";
    String group1 = "groupA";
    String version1 = "1";
    licensesTask.addLicensesFromPom(deps1, new ArtifactInfo(group1, name1, version1));

    licensesTask.writeExtendedDependenciesJson();
    String content = new String(Files.readAllBytes(licensesTask.getExtendedDependenciesJson().toPath()), UTF_8);
    String expected = "[{\"artifactInfo\":{\"group\":\"groupA\",\"version\":\"1\",\"name\":\"deps1\"},\"name\":\"groupA deps1\",\"licenses\":[{\"text\":null,\"url\":\"http://www.opensource.org/licenses/mit-license.php\",\"name\":\"MIT License\"}]}]";
    assertEquals(expected, content);
  }

  @Test
  public void testAddLicensesFromPom_withoutDuplicate() throws IOException, URISyntaxException {
    File deps1 = getResourceFile("dependencies/groupA/deps1.pom");
    String name1 = "deps1";
    String group1 = "groupA";
    String version1 = "1";
    licensesTask.addLicensesFromPom(deps1, new ArtifactInfo(group1, name1, version1));

    File deps2 = getResourceFile("dependencies/groupB/bcd/deps2.pom");
    String name2 = "deps2";
    String group2 = "groupB";
    String version2 = "1";
    licensesTask.addLicensesFromPom(deps2, new ArtifactInfo(group2, name2, version2));

    licensesTask.writeExtendedDependenciesJson();
    String content = new String(Files.readAllBytes(licensesTask.getExtendedDependenciesJson().toPath()), UTF_8);
    String expected = "[{\"artifactInfo\":{\"group\":\"groupA\",\"version\":\"1\",\"name\":\"deps1\"},\"name\":\"groupA deps1\",\"licenses\":[{\"text\":null,\"url\":\"http://www.opensource.org/licenses/mit-license.php\",\"name\":\"MIT License\"}]},{\"artifactInfo\":{\"group\":\"groupB\",\"version\":\"1\",\"name\":\"deps2\"},\"name\":\"groupB deps2\",\"licenses\":[{\"text\":null,\"url\":\"https://www.apache.org/licenses/LICENSE-2.0\",\"name\":\"Apache 2.0\"}]}]";
    assertEquals(expected, content);
  }

  @Test
  public void testAddLicensesFromPom_withMultiple() throws IOException, URISyntaxException {
    File deps1 = getResourceFile("dependencies/groupA/deps1.pom");
    String name1 = "deps1";
    String group1 = "groupA";
    String version1 = "1";
    licensesTask.addLicensesFromPom(deps1, new ArtifactInfo(group1, name1, version1));

    File deps2 = getResourceFile("dependencies/groupE/deps5.pom");
    String name2 = "deps5";
    String group2 = "groupE";
    String version2 = "1";
    licensesTask.addLicensesFromPom(deps2, new ArtifactInfo(group2, name2, version2));

    licensesTask.writeExtendedDependenciesJson();
    String content = new String(Files.readAllBytes(licensesTask.getExtendedDependenciesJson().toPath()), UTF_8);
    String expected = "[{\"artifactInfo\":{\"group\":\"groupA\",\"version\":\"1\",\"name\":\"deps1\"},\"name\":\"groupA deps1\",\"licenses\":[{\"text\":null,\"url\":\"http://www.opensource.org/licenses/mit-license.php\",\"name\":\"MIT License\"}]},{\"artifactInfo\":{\"group\":\"groupE\",\"version\":\"1\",\"name\":\"deps5\"},\"name\":\"groupE deps5\",\"licenses\":[{\"text\":null,\"url\":\"http://www.opensource.org/licenses/mit-license.php\",\"name\":\"MIT License\"},{\"text\":null,\"url\":\"https://www.apache.org/licenses/LICENSE-2.0\",\"name\":\"Apache License 2.0\"}]}]";
    assertEquals(expected, content);
  }

  @Test
  public void testAddLicensesFromPom_withDuplicate() throws IOException, URISyntaxException {
    File deps1 = getResourceFile("dependencies/groupA/deps1.pom");
    String name1 = "deps1";
    String group1 = "groupA";
    String version1 = "1";
    licensesTask.addLicensesFromPom(deps1, new ArtifactInfo(group1, name1, version1));

    File deps2 = getResourceFile("dependencies/groupA/deps1.pom");
    String name2 = "deps1";
    String group2 = "groupA";
    String version2 = "1";
    licensesTask.addLicensesFromPom(deps2, new ArtifactInfo(group2, name2, version2));

    licensesTask.writeExtendedDependenciesJson();
    String content = new String(Files.readAllBytes(licensesTask.getExtendedDependenciesJson().toPath()), UTF_8);
    String expected = "[{\"artifactInfo\":{\"group\":\"groupA\",\"version\":\"1\",\"name\":\"deps1\"},\"name\":\"groupA deps1\",\"licenses\":[{\"text\":null,\"url\":\"http://www.opensource.org/licenses/mit-license.php\",\"name\":\"MIT License\"}]}]";
    assertEquals(expected, content);
  }

  private File getResourceFile(String resourcePath) throws URISyntaxException {
    return new File(getClass().getClassLoader().getResource(resourcePath).toURI().getPath());
  }

  @Test
  public void testGetBytesFromInputStream_throwException() throws IOException {
    InputStream inputStream = mock(InputStream.class);
    when(inputStream.read(any(byte[].class), anyInt(), anyInt())).thenThrow(new IOException());
    try {
      LicensesTask.getBytesFromInputStream(inputStream, 1, 1);
      fail("This test should throw Exception.");
    } catch (RuntimeException e) {
      assertEquals("Failed to read license text.", e.getMessage());
    }
  }

  @Test
  public void testGetBytesFromInputStream_normalText() {
    String test = "test";
    InputStream inputStream = new ByteArrayInputStream(test.getBytes(UTF_8));
    String content = new String(LicensesTask.getBytesFromInputStream(inputStream, 1, 1), UTF_8);
    assertEquals("e", content);
  }

  @Test
  public void testGetBytesFromInputStream_specialCharacters() {
    String test = "Copyright \u00A9 1991-2017 Unicode";
    InputStream inputStream = new ByteArrayInputStream(test.getBytes(UTF_8));
    String content = new String(LicensesTask.getBytesFromInputStream(inputStream, 4, 18), UTF_8);
    assertEquals("right \u00A9 1991-2017", content);
  }

  @Test
  public void testAddGooglePlayServiceLicenses() throws IOException {
    File outputFile = temporaryFolder.newFolder();
    File tempOutput = new File(outputFile, "dependencies/groupC");
    tempOutput.mkdirs();
    createLicenseZip(tempOutput.getPath() + "play-services-foo-license.aar");
    File artifact = new File(tempOutput.getPath() + "play-services-foo-license.aar");
    licensesTask.addGooglePlayServiceLicenses(artifact);

    licensesTask.writeExtendedDependenciesJson();
    String content = new String(Files.readAllBytes(licensesTask.getExtendedDependenciesJson().toPath()), UTF_8);
    String expected = "[{\"artifactInfo\":null,\"name\":\"safeparcel\",\"licenses\":[{\"text\":\"safeparcel\",\"url\":null,\"name\":null}]},{\"artifactInfo\":null,\"name\":\"JSR 305\",\"licenses\":[{\"text\":\"JSR 305\",\"url\":null,\"name\":null}]}]";
    assertEquals(expected, content);
  }

  @Test
  public void testAddGooglePlayServiceLicenses_withoutDuplicate() throws IOException {
    File outputFile = temporaryFolder.newFolder();
    File groupC = new File(outputFile, "dependencies/groupC");
    groupC.mkdirs();
    createLicenseZip(groupC.getPath() + "/play-services-foo-license.aar");
    File artifactFoo = new File(groupC.getPath() + "/play-services-foo-license.aar");

    File groupD = new File(outputFile, "dependencies/groupD");
    groupD.mkdirs();
    createLicenseZip(groupD.getPath() + "/play-services-bar-license.aar");
    File artifactBar = new File(groupD.getPath() + "/play-services-bar-license.aar");

    licensesTask.addGooglePlayServiceLicenses(artifactFoo);
    licensesTask.addGooglePlayServiceLicenses(artifactBar);

    licensesTask.writeExtendedDependenciesJson();
    String content = new String(Files.readAllBytes(licensesTask.getExtendedDependenciesJson().toPath()), UTF_8);
    String expected = "[{\"artifactInfo\":null,\"name\":\"safeparcel\",\"licenses\":[{\"text\":\"safeparcel\",\"url\":null,\"name\":null}]},{\"artifactInfo\":null,\"name\":\"JSR 305\",\"licenses\":[{\"text\":\"JSR 305\",\"url\":null,\"name\":null}]}]";
    assertEquals(expected, content);
  }

  private void createLicenseZip(String name) throws IOException {
    File zipFile = new File(name);
    ZipOutputStream output = new ZipOutputStream(new FileOutputStream(zipFile));
    File input = new File(BASE_DIR + "/sampleLicenses");
    for (File file : input.listFiles()) {
      ZipEntry entry = new ZipEntry(file.getName());
      byte[] bytes = Files.readAllBytes(file.toPath());
      output.putNextEntry(entry);
      output.write(bytes, 0, bytes.length);
      output.closeEntry();
    }
    output.close();
  }

  @Test
  public void testDependenciesWithNameDuplicatedNames() throws IOException, URISyntaxException {
    File deps6 = getResourceFile("dependencies/groupF/deps6.pom");
    String name1 = "deps6";
    String group1 = "groupF";
    String version1 = "1";
    licensesTask.addLicensesFromPom(deps6, new ArtifactInfo(group1, name1, version1));

    File deps7 = getResourceFile("dependencies/groupF/deps7.pom");
    String name2 = "deps7";
    String group2 = "groupF";
    String version2 = "1";
    licensesTask.addLicensesFromPom(deps7, new ArtifactInfo(group2, name2, version2));

    licensesTask.writeExtendedDependenciesJson();
    String expected = "[{\"artifactInfo\":{\"group\":\"groupF\",\"version\":\"1\",\"name\":\"deps6\"},\"name\":\"Collision Test\",\"licenses\":[{\"text\":null,\"url\":\"http://www.opensource.org/licenses/mit-license.php\",\"name\":\"MIT License\"}]},{\"artifactInfo\":{\"group\":\"groupF\",\"version\":\"1\",\"name\":\"deps7\"},\"name\":\"Collision Test\",\"licenses\":[{\"text\":null,\"url\":\"https://www.apache.org/licenses/LICENSE-2.0\",\"name\":\"Apache License 2.0\"}]}]";
    String content = new String(Files.readAllBytes(licensesTask.getExtendedDependenciesJson().toPath()),
            UTF_8);
    assertEquals(expected, content);
  }

  @Test
  public void action_absentDependencies_rendersAbsentData() throws Exception {
    File dependenciesJson = temporaryFolder.newFile();
    ArtifactInfo[] artifactInfoArray = new ArtifactInfo[] { DependencyUtil.ABSENT_ARTIFACT };
    Gson gson = new Gson();
    try (FileWriter writer = new FileWriter(dependenciesJson)) {
      gson.toJson(artifactInfoArray, writer);
    }
    licensesTask.getDependenciesJson().set(dependenciesJson);

    licensesTask.action();

    String expected = "[{\"artifactInfo\":null,\"name\":\"Debug License Info\",\"licenses\":[{\"text\":\"Licenses are only provided in build variants (e.g. release) where the Android Gradle Plugin generates an app dependency list.\",\"url\":null,\"name\":null}]}]";
    String content = new String(Files.readAllBytes(licensesTask.getExtendedDependenciesJson().toPath()),
            UTF_8);
    assertEquals(expected, content);
  }

}
