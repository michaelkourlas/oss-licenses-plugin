package net.kourlas.oss.licenses.plugin

class ExtendedArtifactInfo {
    private String name
    private ArtifactInfo artifactInfo
    private boolean pom
    private Set<String> licenses

    ExtendedArtifactInfo(String name,
                         ArtifactInfo artifactInfo,
                         boolean pom,
                         Set<String> licenses) {
        this.name = name
        this.artifactInfo = artifactInfo
        this.pom = pom
        this.licenses = licenses
    }

    String getName() {
        return name
    }

    ArtifactInfo getArtifactInfo() {
        return artifactInfo
    }

    boolean isPom() {
        return pom
    }

    Set<String> getLicenses() {
        return licenses
    }

    boolean equals(o) {
        if (this.is(o)) return true
        if (getClass() != o.class) return false

        ExtendedArtifactInfo that = (ExtendedArtifactInfo) o

        if (pom != that.pom) return false
        if (artifactInfo != that.artifactInfo) return false
        if (licenses != that.licenses) return false
        if (name != that.name) return false

        return true
    }

    int hashCode() {
        int result
        result = name.hashCode()
        result = 31 * result + (artifactInfo != null ? artifactInfo.hashCode() : 0)
        result = 31 * result + (pom ? 1 : 0)
        result = 31 * result + licenses.hashCode()
        return result
    }
}
