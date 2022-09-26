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
