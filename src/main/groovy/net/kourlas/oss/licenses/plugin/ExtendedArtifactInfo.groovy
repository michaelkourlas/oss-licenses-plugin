/**
 * Copyright (C) 2022 Michael Kourlas
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

class ExtendedArtifactInfo {
    private String name
    private ArtifactInfo artifactInfo
    private Set<LicenseInfo> licenses

    ExtendedArtifactInfo(String name,
                         ArtifactInfo artifactInfo,
                         Set<LicenseInfo> licenses) {
        this.name = name
        this.artifactInfo = artifactInfo
        this.licenses = licenses
    }

    String getName() {
        return name
    }

    ArtifactInfo getArtifactInfo() {
        return artifactInfo
    }

    Set<LicenseInfo> getLicenses() {
        return licenses
    }

    boolean equals(o) {
        if (this.is(o)) return true
        if (getClass() != o.class) return false

        ExtendedArtifactInfo that = (ExtendedArtifactInfo) o

        if (artifactInfo != that.artifactInfo) return false
        if (licenses != that.licenses) return false
        if (name != that.name) return false

        return true
    }

    int hashCode() {
        int result
        result = name.hashCode()
        result = 31 * result + (artifactInfo != null ? artifactInfo.hashCode() : 0)
        result = 31 * result + licenses.hashCode()
        return result
    }
}
