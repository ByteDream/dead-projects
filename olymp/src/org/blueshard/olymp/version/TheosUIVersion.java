package org.blueshard.olymp.version;

import org.blueshard.olymp.exception.ErrorCodes;
import org.blueshard.olymp.exception.FatalIOException;
import org.blueshard.olymp.files.ConfReader;
import org.blueshard.olymp.files.ServerFiles;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class TheosUIVersion extends Version {

    TreeMap<String, String> versions;
    Set<String> versionsKeySet;

    public TheosUIVersion(String version) throws FatalIOException {
        super(version);

        try {
            versions = new ConfReader.MultipleConfReader(ServerFiles.etc.versions).getAll("TheosUI", true);
        } catch (IOException e) {
            throw new FatalIOException(ErrorCodes.couldnt_read_versions_conf, "Failed to read versions.conf", e);
        }

        versionsKeySet = versions.keySet();
    }

    @Override
    public int toInt() {
        String[] allVersions = versionsKeySet.toArray(new String[versionsKeySet.size()]);

        for (int i = 0; i < allVersions.length; i++) {
            if (allVersions[i].equals(toString())) {
                return i + 1;
            }
        }
        return 0;
    }

    private int toInt(String version) {
        String[] allVersions = versionsKeySet.toArray(new String[versionsKeySet.size()]);

        for (int i = 0; i < allVersions.length; i++) {
            if (allVersions[i].equals(version)) {
                return i + 1;
            }
        }
        return 0;
    }

    public boolean higherThan(String version) {
        return toInt() > toInt(version);
    }

    @Override
    public boolean higherThan(Version version) {
        return toInt() > toInt(version.toString());
    }

    public boolean lowerThan(String version) {
        return toInt() < toInt(version);
    }

    @Override
    public boolean lowerThan(Version version) {
        return toInt() < toInt(version.toString());
    }

    public String getChanges() {
        return versions.get(toString()).split(":")[1];
    }

    public boolean hasOptionalUpdate() throws FatalIOException {
        if (toInt() != currentVersion().toInt()) {
            Map<String, String> reverseVersions = versions.descendingMap();

            for (Map.Entry<String, String> stringStringEntry : reverseVersions.entrySet()) {
                String key = stringStringEntry.getKey();
                String value = stringStringEntry.getValue().split(":")[0];
                if (!key.equals(toString())) {
                    if (value.equals("optional")) {
                        return true;
                    }
                } else {
                    return false;
                }
            }
        }
        return false;
    }

    public boolean hasRequiredUpdate() throws FatalIOException {
        if (toInt() != currentVersion().toInt()) {
            Map<String, String> reverseVersions = versions.descendingMap();

            for (Map.Entry<String, String> stringStringEntry : reverseVersions.entrySet()) {
                String key = stringStringEntry.getKey();
                String value = stringStringEntry.getValue().split(":")[0];
                if (!key.equals(toString())) {
                    if (value.equals("required")) {
                        return true;
                    }
                } else {
                    return false;
                }
            }
        }
        return false;
    }

    public static TheosUIVersion currentVersion() throws FatalIOException {
        try {
            return new TheosUIVersion(new ConfReader.MultipleConfReader(ServerFiles.etc.versions).getAll("TheosUI", true).lastKey());
        } catch (IOException e) {
            throw new FatalIOException(ErrorCodes.couldnt_read_versions_conf, "Failed to read versions.conf", e);
        }
    }

    public static TheosUIVersion toArtificeUIVersion(int versionAsInt) throws IOException, FatalIOException {
        Set<String> versionsKeySet = new ConfReader.MultipleConfReader(ServerFiles.etc.versions).getAll("TheosUI", true).keySet();
        String[] allVersions = versionsKeySet.toArray(new String[versionsKeySet.size()]);

        for (int i = allVersions.length; i > 0; i--) {
            if (i == versionAsInt) {
                return new TheosUIVersion(allVersions[i]);
            }
        }
        return null;
    }

}
