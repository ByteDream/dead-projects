package org.blueshard.sekaijuclt.user;

public class UserParams {

    public final static String USERNAME = "username";
    public final static String PASSWORD = "password";
    public final static String SALT = "salt";
    public final static String EMAIL = "email";

    public static class File {

        public final static String STARTDIRECOTRY = "startDirectory";
        public final static String FILEPATH = "filepath";

    }

    public static class CheckSum {

        public final static String MD5 = "md5";

    }

    public static class Key {

        public final static String PUBLICKEY = "key";

    }

    public static class LogLevel {

        public final static String LOGLEVEL = "logLevel";

        public final static String ALL = "0";
        public final static String WARNING = "1";
        public final static String NOTHING = "2";

    }

    public static class UserLevel {

        public final static String USERLEVEL = "userLevel";

        public final static String PUBLIC = "0";
        public final static String PROTECTED = "1";
        public final static String PRIVATE = "2";

    }

    public static class State {

        public final static String STATE = "state";

        public final static String ACTIVE = "active";
        public final static String DISABLED = "disabled";

    }

}
