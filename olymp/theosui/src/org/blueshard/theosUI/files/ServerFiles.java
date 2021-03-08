package org.blueshard.theosUI.files;

public class ServerFiles {

    public static class logs {

        public final static String dir = "/srv/logs/";
        public final static String main_log = dir + "main.log";

    }

    public static class ssl {

        public final static String dir = "/srv/ssl/";
        public final static String sslKey_cert = dir + "sslKey.cert";
        public final static String sslKeyStore_jks = dir + "sslKeyStore.jks";
        public final static String sslTrustStore_jks = dir + "sslTrustStore.jks";

    }

    public static class users {

        public final static String dir = "/srv/users/";
        public final static String users_conf = dir  + "/users.conf";

        public static class user {

            public final String dir;
            public final String user_conf;
            public final String userfiles_conf;

            public user(String username) {
                dir = "/srv/users/" + username + "/";
                user_conf = dir + "user.conf";
                userfiles_conf = dir + "userfiles.conf";
            }

            public static class userfiles {

                public final String dir;

                public userfiles(String username) {
                    dir = "/srv/users/" + username + "/userfiles/";
                }

            }

        }

    }

}
