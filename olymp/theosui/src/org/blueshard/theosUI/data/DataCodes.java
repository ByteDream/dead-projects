package org.blueshard.theosUI.data;

public class DataCodes {

    public static final int DATACODESLENGHT = 5;

    public static class Client {

        public static final int UNEXPECTEDERROR = 56400;
        public static final int UNEXPECTEDEXIT = 95078;

        public static final int CLOSE = 69826;

        public static final int FIRSTCONNECT = 19938;

        public static final int PUBLICKEY = 19294;

        public static final int UPDATEYES = 80515;
        public static final int UPDATENO = 38510;

        public static final int LOGIN = 39208;
        public static final int REGISTERCODE = 18981;
        public static final int REGISTER = 84219;

        public static final int GETFILESDATA = 28926;
        public static final int GETFILE = 95868;
        public static final int SENDFILE = 53639;

    }

    public static class Server {

        public static final int UNEXPECTEDERROR = 29875;
        public static final int UNEXPECTEDEXIT = 85048;

        public static final int NOTLOGGEDIN = 77015;

        public static final int CLOSE = 42812;

        public static final int FIRSTCONNECT = 76896;

        public static final int PUBLICKEY = 19294;

        public static final int OPTIONALUPDATE = 12925;
        public static final int REQUIREDUPDATE = 97103;

        public static final int LOGINFAIL = 11868;
        public static final int LOGINSUCCESS = 54151;
        public static final int REGISTERCODE_EXIST = 31166;
        public static final int REGISTERCODE_NOT_EXIST = 47648;
        public static final int REGISTERFAIL = 52300;
        public static final int REGISTERFAIL_USER_EXIST= 77444;
        public static final int REGISTERSUCCESS = 34367;

        public static final int RECEIVEFILEFAIL = 45747;
        public static final int RECEIVEFILESUCCESS = 75368;
        public static final int SENDFILESDATA = 78946;
        public static final int SENDFILEFAIL = 90173;
        public static final int SENDFILESSUCCESS = 37272;

    }

    public static class Params {

        public static class CheckSum {

            public static final String MD5 = "md5";

        }

        public static class ClientAgent {

            public static final String CLIENTAGENT = "clientAgent";

            public static final String VALIDAGENT = "validAgent";

            public static final String TheosUI = "theosUI";

        }

        public static class File {

            public static final String STARTDIRECOTRY = "startDirectory";
            public static final String FILEPATH = "filepath";

        }

        public static class Key {

            public static final String PUBLICKEY = "key";

        }

        public static class LogLevel {

            public static final String LOGLEVEL = "logLevel";

            public static final String ALL = "0";
            public static final String WARNING = "1";
            public static final String NOTHING = "2";

        }

        public static class LogReg {

            public static final String USERNAME = "username";
            public static final String PASSWORD = "password";
            public static final String SALT = "salt";
            public static final String EMAIL = "email";

        }

        public static class RegisterCode {

            public static final String REGISTERCODE = "registerCode";

        }

        public static class Update {

            public static final String UPDATE = "update";

            public static final String NEWVERSION = "newVersion";
            public static final String CHANGES = "changes";

        }

        public static class UserLevel {

            public static final String USERLEVEL = "userLevel";

            public static final String PUBLIC = "0";
            public static final String PROTECTED = "1";
            public static final String PRIVATE = "2";

        }

        public static class State {

            public static final String STATE = "state";

            public static final String ACTIVE = "active";
            public static final String DISABLED = "disabled";

        }

        public static class Version {

            public static final String VERSION = "version";

        }

    }

}
