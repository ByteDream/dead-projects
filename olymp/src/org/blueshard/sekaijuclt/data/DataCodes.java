package org.blueshard.sekaijuclt.data;

public class DataCodes {

    public static final int DATACODESLENGHT = 5;

    public static class Client {

        public static final int UNEXPECTEDERROR = 56400;
        public static final int UNEXPECTEDEXIT = 95078;

        public static final int EXIT = 69826;

        public static final int PUBLICKEY = 19294;

        public static final int LOGIN = 39208;
        public static final int REGISTER = 84219;

        public static final int GETFILESDATA = 28926;
        public static final int GETFILE = 95868;
        public static final int SENDFILE = 53639;

    }

    public static class Server {

        public static final int UNEXPECTEDERROR = 29875;
        public static final int UNEXPECTEDEXIT = 85048;

        public static final int NOTLOGGEDIN = 77015;

        public static final int EXIT = 42812;

        public static final int PUBLICKEY = 19294;

        public static final int LOGINFAIL = 11868;
        public static final int LOGINSUCCESS = 54151;
        public static final int REGISTERFAIL = 52300;
        public static final int REGISTERFAIL_USER_EXIST= 77444;
        public static final int REGISTERSUCCESS = 34367;

        public static final int RECEIVEFILEFAIL = 45747;
        public static final int RECEIVEFILESUCCESS = 75368;
        public static final int SENDFILESDATA = 78946;
        public static final int SENDFILEFAIL = 90173;
        public static final int SENDFILESSUCCESS = 37272;

    }

}
