package utils;

/**
 * Application's constants.
 * 
 * @author Thanh.Nguyen <btnguyen2k@gmail.com>
 */
public class Constants {

    public final static String APP_NAME = "MYPAGES";

    public final static String COOKIE_FB_ACCESS_TOKEN = "FB_ACCESS_TOKEN";
    public final static String COOKIE_FB_ACCESS_TOKEN_EXPIRY = "FB_ACCESS_TOKEN_EXPIRY";

    public final static String SESSION_FB_ACCESS_TOKEN = "FB_AT";
    public final static String SESSION_FB_ACCESS_TOKEN_TIME = "FB_AT_TIME";
    public final static String SESSION_FB_PAGES = "FB_PGS";
    public final static String SESSION_ACC_LAST_ACTIVE = "ACC_ACT_TIME";

    public final static String REDIS_SET_ACTIVE_ACCOUNTS = APP_NAME + "_ACTIVE_ACCOUNTS";
}
