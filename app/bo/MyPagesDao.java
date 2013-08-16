package bo;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;

import play.cache.Cache;
import play.db.DB;
import utils.DPathUtils;
import utils.JsonUtils;

public class MyPagesDao {

    private static JdbcTemplate jdbcTemplate;

    public final static String TABLE_ACCOUNT = "mypages_account";
    public final static String COL_ACCOUNT_EMAIL = "account_email";
    public final static String COL_ACCOUNT_TIMESTAMP = "timestamp_create";

    public final static String TABLE_PAGE = "mypages_page";
    public final static String COL_PAGE_ID = "page_id";
    public final static String COL_PAGE_ADMIN = "admin_email";
    public final static String COL_PAGE_TIMESTAMP_CREATE = "timestamp_create";
    public final static String COL_PAGE_TIMESTAMP_ACTIVE = "timestamp_lastactive";
    public final static String COL_PAGE_STATUS = "page_status";
    public final static String COL_PAGE_SETTINGS = "page_settings";
    public final static String PAGE_SETTING_SIGNATURE = "signature";

    private static JdbcTemplate jdbcTemplate() {
        if (jdbcTemplate == null) {
            jdbcTemplate = new JdbcTemplate(DB.getDataSource());
        }
        return jdbcTemplate;
    }

    private static String cacheKeyAccount(String email) {
        return "ACCOUNT_" + email;
    }

    private static String cacheKeyPage(String pageId, String email) {
        return "PAGE_" + pageId + "_" + email;
    }

    public static void createAccount(String email) {
        String SQL = "INSERT IGNORE INTO {0} (uemail) VALUES (?)";
        JdbcTemplate jdbcTemplate = jdbcTemplate();
        jdbcTemplate.update(MessageFormat.format(SQL, TABLE_ACCOUNT), new Object[] { email });
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> getAccount(String email) {
        final String CACHE_KEY = cacheKeyAccount(email);
        Map<String, Object> result = (Map<String, Object>) Cache.get(CACHE_KEY);
        if (result == null) {
            final String SQL = "SELECT uemail AS {1}, utimestamp_create AS {2} FROM {0} WHERE uemail=?";
            JdbcTemplate jdbcTemplate = jdbcTemplate();
            List<Map<String, Object>> dbResult = jdbcTemplate.queryForList(MessageFormat.format(
                    SQL, TABLE_ACCOUNT, COL_ACCOUNT_EMAIL, COL_ACCOUNT_TIMESTAMP),
                    new Object[] { email });
            result = dbResult != null && dbResult.size() > 0 ? dbResult.get(0) : null;
            if (result != null) {
                Cache.set(CACHE_KEY, result);
            }
        }
        return result;
    }

    public static void createPage(String pageId, String email) {
        String SQL = "INSERT IGNORE INTO {0} (pid, padmin_email, ptimestamp_lastactive, psettings) VALUES (?, ?, NOW(), ?)";
        JdbcTemplate jdbcTemplate = jdbcTemplate();
        jdbcTemplate.update(MessageFormat.format(SQL, TABLE_PAGE), new Object[] { pageId, email,
                "{}" });
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> getPage(String pageId, String email) {
        final String CACHE_KEY = cacheKeyPage(pageId, email);
        Map<String, Object> result = (Map<String, Object>) Cache.get(CACHE_KEY);
        if (result == null) {
            final String SQL = "SELECT pid AS {1}, padmin_email AS {2}, ptimestamp_create AS {3}, ptimestamp_lastactive AS {4}, pstatus AS {5}, psettings AS {6} FROM {0} WHERE pid=? AND padmin_email=?";
            JdbcTemplate jdbcTemplate = jdbcTemplate();
            List<Map<String, Object>> dbResult = jdbcTemplate.queryForList(MessageFormat.format(
                    SQL, TABLE_PAGE, COL_PAGE_ID, COL_PAGE_ADMIN, COL_PAGE_TIMESTAMP_CREATE,
                    COL_PAGE_TIMESTAMP_ACTIVE, COL_PAGE_STATUS, COL_PAGE_SETTINGS), new Object[] {
                    pageId, email });
            result = dbResult != null && dbResult.size() > 0 ? dbResult.get(0) : null;
            if (result != null) {
                Cache.set(CACHE_KEY, result);
            }
        }
        return result;
    }

    private static void updatePage(Map<String, Object> pageData) {
        final String SQL = "UPDATE IGNORE {0} SET ptimestamp_lastactive=NOW(), pstatus=?, psettings=? WHERE pid=? AND padmin_email=?";
        Integer pStatus = DPathUtils.getValue(pageData, COL_PAGE_STATUS, Integer.class);
        String pSettings = DPathUtils.getValue(pageData, COL_PAGE_SETTINGS, String.class);
        String pId = DPathUtils.getValue(pageData, COL_PAGE_ID, String.class);
        String pAdminEmail = DPathUtils.getValue(pageData, COL_PAGE_ADMIN, String.class);
        JdbcTemplate jdbcTemplate = jdbcTemplate();
        jdbcTemplate.update(MessageFormat.format(SQL, TABLE_PAGE), pStatus, pSettings, pId,
                pAdminEmail);
        final String CACHE_KEY = cacheKeyPage(pId, pAdminEmail);
        Cache.remove(CACHE_KEY);
    }

    @SuppressWarnings("unchecked")
    public static void updatePageSignature(String pageId, String email, String signature) {
        Map<String, Object> pageData = getPage(pageId, email);
        if (pageData != null) {
            String strPageSettings = DPathUtils.getValue(pageData, PAGE_SETTING_SIGNATURE,
                    String.class);
            Map<String, Object> pageSettings = null;
            try {
                pageSettings = JsonUtils.fromJson(strPageSettings, Map.class);
            } catch (Exception e) {
                pageSettings = new HashMap<String, Object>();
            }
            if (!(pageSettings instanceof Map)) {
                pageSettings = new HashMap<String, Object>();
            }
            DPathUtils.setValue(pageSettings, PAGE_SETTING_SIGNATURE, signature);
            pageData.put(COL_PAGE_SETTINGS, JsonUtils.toJson(pageSettings));
            updatePage(pageData);
        }
    }
}
