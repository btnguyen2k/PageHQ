package actors;

import java.sql.Connection;
import java.text.MessageFormat;
import java.util.Calendar;

import javax.sql.DataSource;

import play.db.DB;
import akka.actor.UntypedActor;

/**
 * Automatically create new mypages_feed_xxx per week.
 * 
 * @author Thanh Nguyen
 */
public class FeedDbTableActor extends UntypedActor {

	private final static String SQL = "CREATE TABLE IF NOT EXISTS mypages_feed_{0} LIKE mypages_master_feed";

	@Override
	public void onReceive(Object msg) throws Exception {
		DataSource ds = DB.getDataSource();
		Connection conn = ds.getConnection();
		try {
			conn.setAutoCommit(true);
			Calendar cal = Calendar.getInstance();
			int year = cal.get(Calendar.YEAR);
			int week = cal.get(Calendar.WEEK_OF_YEAR);
			String shard = "" + year + (week < 10 ? "0" + week : week);
			String sql = MessageFormat.format(SQL, shard);
			conn.createStatement().execute(sql);
		} finally {
			conn.close();
		}
	}

}
