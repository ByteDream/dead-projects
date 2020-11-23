package org.blueshard.olymp.version;

public class ServerVersion extends Version {

    public ServerVersion(String version) {
        super(version);
    }

    @Override
    public int toInt() {
        switch (toString()) {
            case "0.1.0":
                return 1;
            default:
                return 0;
        }
    }

    public boolean higherThan(String version) {
        return higherThan(new ServerVersion(version));
    }

    @Override
    public boolean higherThan(Version version) {
        return toInt() > version.toInt();
    }

    public boolean lowerThan(String version) {
        return lowerThan(new ServerVersion(version));
    }

    @Override
    public boolean lowerThan(Version version) {
        return toInt() < version.toInt();
    }
    public static ServerVersion currentVersion() {
        return new ServerVersion("0.1.0");
    }

}
