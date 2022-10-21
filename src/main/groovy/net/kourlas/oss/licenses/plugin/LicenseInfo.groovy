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

class LicenseInfo {
    String name
    String url
    String text

    LicenseInfo(String name, String url, String text) {
        this.name = name
        this.url = url
        this.text = text
    }

    String getName() {
        return name
    }

    String getUrl() {
        return url
    }

    String getText() {
        return text
    }

    boolean equals(o) {
        if (this.is(o)) return true
        if (getClass() != o.class) return false

        LicenseInfo that = (LicenseInfo) o

        if (name != that.name) return false
        if (text != that.text) return false
        if (url != that.url) return false

        return true
    }

    int hashCode() {
        int result
        result = (name != null ? name.hashCode() : 0)
        result = 31 * result + (url != null ? url.hashCode() : 0)
        result = 31 * result + (text != null ? text.hashCode() : 0)
        return result
    }
}
