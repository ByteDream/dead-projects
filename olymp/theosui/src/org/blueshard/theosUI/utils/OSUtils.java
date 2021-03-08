package org.blueshard.theosUI.utils;

import com.sun.javafx.PlatformUtil;

import java.io.File;

public class OSUtils {

    public static File getConfigFile() {
        if (PlatformUtil.isLinux() || PlatformUtil.isUnix()) {
            return new File(System.getProperty("user.home") + ".theos/theos.conf");
        } else if (PlatformUtil.isWindows()) {
            return new File("C:\\Users\\" + System.getProperty("user.name") + "\\AppData\\Roaming\\Theos\\theos.config");
        } else {
            return new File("theos.config");
        }
    }

    public static File getLogFile() {
        if (PlatformUtil.isLinux() || PlatformUtil.isUnix()) {
            return new File(System.getProperty("user.home") + ".theos/theos.log");
        } else if (PlatformUtil.isWindows()) {
            return new File("C:\\Users\\" + System.getProperty("user.name") + "\\AppData\\Theos\\theos.log");
        } else {
            return new File("theos.log");
        }
    }

}
