package org.blueshard.olymp.files;

public class ServerFiles {

    public static class etc {

        public static final String dir = "/srv/etc/";
        public static final String register_codes = dir + "register_codes";
        public static final String versions = dir + "versions.conf";

        public static class update {

            public static final String dir = "/srv/etc/update/";
            public static final String TheosUI_jar = dir + "update.jar";

        }

    }

    public static class logs {

        public static final String dir = "/srv/logs/";
        public static final String main_log = dir + "main.log";

    }

    public static class user_files {

        public static final String dir = "/srv/user_files/";

        public final String user_files_dir;

        public user_files(String UUID) {
            user_files_dir = dir + UUID + "/";
        }

    }

}
