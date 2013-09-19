package actors;

import java.sql.Connection;
import java.text.MessageFormat;

import javax.sql.DataSource;

import play.db.DB;
import utils.Utils;
import bo.MyPagesDao;

/**
 * Automatically create new mypages_feed_xxx per week.
 * 
 * @author Thanh Nguyen
 */
public class FeedDbTableActor extends BaseScheduledActor {

    private final static String SQL = "CREATE TABLE IF NOT EXISTS {0} LIKE mypages_master_feed";

    @Override
    protected void internalOnReceive(Object msg) throws Exception {
        String shard = Utils.shardWeekly();
        String tableName = MessageFormat.format(MyPagesDao.TABLE_FEED, shard);
        DataSource ds = DB.getDataSource();
        Connection conn = ds.getConnection();
        try {
            conn.setAutoCommit(true);
            String sql = MessageFormat.format(SQL, tableName);
            conn.createStatement().execute(sql);
        } finally {
            conn.close();
        }
    }

}
