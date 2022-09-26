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
