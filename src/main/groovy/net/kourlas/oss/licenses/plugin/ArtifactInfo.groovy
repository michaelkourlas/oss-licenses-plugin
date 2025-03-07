/**
 * Copyright 2018 Google LLC
 * Modifications copyright (C) 2022 Michael Kourlas
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

class ArtifactInfo {
    private String group
    private String name
    private String version

    ArtifactInfo(String group,
                 String name,
                 String version) {
        this.group = group
        this.name = name
        this.version = version
    }

    String getGroup() {
        return group
    }

    String getName() {
        return name
    }

    String getVersion() {
        return version
    }

    @Override
    boolean equals(Object obj) {
        if (obj instanceof ArtifactInfo) {
            return (group == obj.group
                    && name == obj.name
                    && version == obj.version)
        }
        return false
    }

    @Override
    int hashCode() {
        return group.hashCode() ^ name.hashCode() ^ version.hashCode()
    }

    @Override
    String toString() {
        return "$group:$name:$version"
    }
}
