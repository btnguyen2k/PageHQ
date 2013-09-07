package bo;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;

import utils.Utils;

import com.github.ddth.plommon.bo.BaseDao;
import com.github.ddth.plommon.utils.DPathUtils;
import com.github.ddth.plommon.utils.JsonUtils;

public class MyPagesDao extends BaseDao {

    public final static String TABLE_ACCOUNT = "mypages_account";

    public final static String TABLE_PAGE = "mypages_page";

    public final static String TABLE_FEED = "mypages_feed_{0}";

    /*--------------------------------------------------------------------------------*/

    private static String cacheKeyAccount(String email) {
        return "ACCOUNT_" + email;
    }

    private static String cacheKeyPage(String pageId, String email) {
        return "PAGE_" + pageId + "_" + email;
    }

    private static String cacheKeyFeed(String id) {
        return "FEED_" + id;
    }

    /*--------------------------------------------------------------------------------*/

    /*--------------------------------------------------------------------------------*/
    /**
     * Creates a new user account.
     * 
     * @param email
     */
    public static void createAccount(String email) {
        String SQL = "INSERT IGNORE INTO {0} (uemail) VALUES (?)";
        JdbcTemplate jdbcTemplate = jdbcTemplate();
        jdbcTemplate.update(MessageFormat.format(SQL, TABLE_ACCOUNT), new Object[] { email });
        removeFromCache(cacheKeyAccount(email));
    }

    /**
     * Loads a user account.
     * 
     * @param email
     * @return
     */
    @SuppressWarnings("unchecked")
    public static AccountBo getAccount(String email) {
        final String CACHE_KEY = cacheKeyAccount(email);
        Map<String, Object> dbRow = getFromCache(CACHE_KEY, Map.class);
        if (dbRow == null) {
            final String SQL = "SELECT uemail AS {1}, utimestamp_create AS {2} FROM {0} WHERE uemail=?";
            JdbcTemplate jdbcTemplate = jdbcTemplate();
            List<Map<String, Object>> dbResult = jdbcTemplate.queryForList(MessageFormat.format(
                    SQL, TABLE_ACCOUNT, AccountBo.COL_ACCOUNT_EMAIL,
                    AccountBo.COL_ACCOUNT_TIMESTAMP), new Object[] { email });
            dbRow = dbResult != null && dbResult.size() > 0 ? dbResult.get(0) : null;
            putToCache(CACHE_KEY, dbRow);
        }
        return dbRow != null ? (AccountBo) new AccountBo().fromMap(dbRow) : null;
    }

    /*--------------------------------------------------------------------------------*/

    /*--------------------------------------------------------------------------------*/

    /**
     * Creates a new FB page.
     * 
     * @param pageId
     * @param email
     */
    public static void createPage(String pageId, String email) {
        String SQL = "INSERT IGNORE INTO {0} (pid, padmin_email, ptimestamp_lastactive, psettings) VALUES (?, ?, NOW(), ?)";
        JdbcTemplate jdbcTemplate = jdbcTemplate();
        jdbcTemplate.update(MessageFormat.format(SQL, TABLE_PAGE), new Object[] { pageId, email,
                "{}" });
        removeFromCache(cacheKeyPage(pageId, email));
    }

    /**
     * Loads an FB page.
     * 
     * @param pageId
     * @param email
     * @return
     */
    @SuppressWarnings("unchecked")
    public static PageBo getPage(String pageId, String email) {
        final String CACHE_KEY = cacheKeyPage(pageId, email);
        Map<String, Object> dbRow = getFromCache(CACHE_KEY, Map.class);
        if (dbRow == null) {
            final String SQL = "SELECT pid AS {1}, padmin_email AS {2}, ptimestamp_create AS {3}, ptimestamp_lastactive AS {4}, pstatus AS {5}, psettings AS {6} FROM {0} WHERE pid=? AND padmin_email=?";
            JdbcTemplate jdbcTemplate = jdbcTemplate();
            List<Map<String, Object>> dbResult = jdbcTemplate.queryForList(MessageFormat.format(
                    SQL, TABLE_PAGE, PageBo.COL_PAGE_ID, PageBo.COL_PAGE_ADMIN,
                    PageBo.COL_PAGE_TIMESTAMP_CREATE, PageBo.COL_PAGE_TIMESTAMP_ACTIVE,
                    PageBo.COL_PAGE_STATUS, PageBo.COL_PAGE_SETTINGS),
                    new Object[] { pageId, email });
            dbRow = dbResult != null && dbResult.size() > 0 ? dbResult.get(0) : null;
            putToCache(CACHE_KEY, dbRow);
        }
        return dbRow != null ? (PageBo) new PageBo().fromMap(dbRow) : null;
    }

    /**
     * Updates an existing FB page.
     * 
     * @param pageData
     */
    private static PageBo updatePage(PageBo page) {
        final String SQL = "UPDATE IGNORE {0} SET ptimestamp_lastactive=NOW(), pstatus=?, psettings=? WHERE pid=? AND padmin_email=?";
        Integer pStatus = page.getStatus();
        String pSettings = page.getSettings();
        String pId = page.getId();
        String pAdminEmail = page.getAdminEmail();
        JdbcTemplate jdbcTemplate = jdbcTemplate();
        jdbcTemplate.update(MessageFormat.format(SQL, TABLE_PAGE), pStatus, pSettings, pId,
                pAdminEmail);
        Map<String, Object> dbRow = page.toMap();
        final String CACHE_KEY = cacheKeyPage(pId, pAdminEmail);
        putToCache(CACHE_KEY, dbRow);
        // removeFromCache(CACHE_KEY);
        return page;
    }

    /**
     * Updates FB page's signature attribute.
     * 
     * @param page
     * @param newSignature
     * @return
     */
    @SuppressWarnings("unchecked")
    public static PageBo updatePageSignature(PageBo page, String newSignature) {
        String strPageSettings = page.getSettings();
        Map<String, Object> pageSettings = null;
        try {
            pageSettings = JsonUtils.fromJsonString(strPageSettings, Map.class);
        } catch (Exception e) {
            pageSettings = new HashMap<String, Object>();
        }
        if (!(pageSettings instanceof Map)) {
            pageSettings = new HashMap<String, Object>();
        }
        DPathUtils.setValue(pageSettings, PageBo.PAGE_SETTING_SIGNATURE, newSignature);
        page.setSettings(JsonUtils.toJsonString(pageSettings));
        return updatePage(page);
    }

    /**
     * Updates FB page's signature attribute.
     * 
     * @param pageId
     * @param email
     * @param newSignature
     */
    public static PageBo updatePageSignature(String pageId, String email, String newSignature) {
        PageBo page = getPage(pageId, email);
        if (page != null) {
            return updatePageSignature(page, newSignature);
        }
        return null;
    }

    /*--------------------------------------------------------------------------------*/

    /*--------------------------------------------------------------------------------*/

    /**
     * Creates a new FB feed.
     * 
     * @param feedId
     * @param feedType
     * @param metaInfo
     * @param userEmail
     * @param pageId
     */
    public static void createFeed(String feedId, int feedType, Map<String, Object> metaInfo,
            String userEmail, String pageId) {
        String shard = Utils.shardWeekly();
        String tableName = MessageFormat.format(TABLE_FEED, shard);
        String id = shard + "_" + feedId;
        String SQL = "INSERT IGNORE INTO {0} "
                + "(fid, feed_id, feed_type, fmetainfo, fuser_email, fpage_id, ftimestamp, fnum_likes, fnum_shares, fnum_comments) "
                + "VALUES (?, ?, ?, ?, ?, ?, NOW(), 0, 0, 0)";
        JdbcTemplate jdbcTemplate = jdbcTemplate();
        jdbcTemplate.update(MessageFormat.format(SQL, tableName), new Object[] { id, feedId,
                feedType, JsonUtils.toJsonString(metaInfo), userEmail, pageId });
        removeFromCache(cacheKeyFeed(id));
    }

    /**
     * id Loads a FB feed.
     * 
     * @param feedId
     * @return
     */
    @SuppressWarnings("unchecked")
    public static FeedBo getFeed(String id) {
        final String CACHE_KEY = cacheKeyFeed(id);
        Map<String, Object> dbRow = getFromCache(CACHE_KEY, Map.class);
        if (dbRow == null) {
            final String SQL = "SELECT fid AS {1}, feed_id AS {2}, feed_type AS {3}, fmetainfo AS {4}, "
                    + "fuser_email AS {5}, fpage_id AS {6}, ftimestamp AS {7}, "
                    + "fnum_likes AS {8}, fnum_shares AS {9}, fnum_comments AS {10} "
                    + "FROM {0} WHERE fid=?";
            JdbcTemplate jdbcTemplate = jdbcTemplate();
            List<Map<String, Object>> dbResult = jdbcTemplate.queryForList(MessageFormat.format(
                    SQL, TABLE_FEED, FeedBo.COL_ID, FeedBo.COL_FEED_ID, FeedBo.COL_FEED_TYPE,
                    FeedBo.COL_FEED_METAINFO, FeedBo.COL_USER_EMAIL, FeedBo.COL_PAGE_ID,
                    FeedBo.COL_TIMESTAMP_CREATE, FeedBo.COL_NUM_LIKES, FeedBo.COL_NUM_SHARES,
                    FeedBo.COL_NUM_COMMENTS), new Object[] { id });
            dbRow = dbResult != null && dbResult.size() > 0 ? dbResult.get(0) : null;
            putToCache(CACHE_KEY, dbRow);
        }
        return dbRow != null ? (FeedBo) new FeedBo().fromMap(dbRow) : null;
    }
    /*--------------------------------------------------------------------------------*/
}
