package net.kourlas.oss.licenses.plugin

class ExtendedArtifactInfo extends ArtifactInfo {
    private String prettyName
    private String licenseName

    ExtendedArtifactInfo(String group,
                         String name,
                         String version,
                         String prettyName,
                         String licenseName) {
        super(group, name, version)

        this.prettyName = prettyName
        this.licenseName = licenseName
    }

    String getPrettyName() {
        return prettyName
    }

    String getLicenseName() {
        return licenseName
    }

    @Override
    boolean equals(Object obj) {
        if (!super.equals(obj)) {
            return false
        }
        if (obj instanceof ExtendedArtifactInfo) {
            return (prettyName == obj.prettyName
                    && licenseName == obj.licenseName)
        }
        return false
    }

    @Override
    int hashCode() {
        return (super.hashCode()
                ^ prettyName.hashCode()
                ^ licenseName.hashCode())
    }
}
