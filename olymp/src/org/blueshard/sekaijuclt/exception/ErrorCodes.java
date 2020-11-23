package org.blueshard.sekaijuclt.exception;

import java.util.HashSet;
import java.util.Set;

public class ErrorCodes {

    public final static int couldnt_read_users_conf = 545;
    public final static int couldnt_write_users_conf = 376;

    public final static int couldnt_create_user_folder = 639;
    public final static int couldnt_delete_user_folder = 152;

    public final static int couldnt_create_user_userfiles_folder = 293;
    public final static int couldnt_delete_user_userfiles_folder = 219;

    public final static int couldnt_create_user_user_conf = 934;
    public final static int couldnt_read_user_user_conf = 177;
    public final static int couldnt_write_user_user_conf = 990;
    public final static int couldnt_delete_user_user_conf = 680;

    public final static int couldnt_create_user_user_files_conf = 353;
    public final static int couldnt_read_user_user_files_conf = 155;
    public final static int couldnt_write_user_user_files_conf = 476;
    public final static int couldnt_delete_user_user_files_conf = 559;

    public final static int couldnt_delete_custom_user_directory = 383;
    public final static int couldnt_delete_custom_user_file = 942;

    public final static HashSet<Integer> allFatalUserLoginRegisterErrors = new HashSet<>(Set.of(couldnt_read_users_conf, couldnt_write_users_conf,
            couldnt_create_user_folder, couldnt_delete_user_folder,
            couldnt_create_user_userfiles_folder, couldnt_delete_user_userfiles_folder,
            couldnt_create_user_user_conf, couldnt_read_user_user_conf, couldnt_write_user_user_conf, couldnt_delete_user_user_conf,
            couldnt_create_user_user_files_conf, couldnt_read_user_user_files_conf, couldnt_write_user_user_files_conf, couldnt_delete_user_user_files_conf,
            couldnt_delete_custom_user_directory, couldnt_delete_custom_user_file));

}
