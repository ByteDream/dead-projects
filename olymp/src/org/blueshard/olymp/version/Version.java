package org.blueshard.olymp.version;

public abstract class Version {

    private final String version;

    public Version(String version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return version;
    }

    public abstract int toInt();

    public abstract boolean higherThan(Version version);

    public abstract boolean lowerThan(Version version);
}
