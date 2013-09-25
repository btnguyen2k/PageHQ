package utils;

import java.util.Calendar;

/**
 * Misc utilities.
 * 
 * @author Thanh.Nguyen <btnguyen2k@gmail.com>
 */
public class Utils {

    /**
     * Computes weekly shard id.
     * 
     * @param delta
     *            plus/minus this number of weeks from the current week_of_year
     * @return
     */
    public static String shardWeekly(int delta) {
        Calendar cal = Calendar.getInstance();
        if (delta != 0) {
            cal.add(Calendar.WEEK_OF_YEAR, delta);
        }
        int year = cal.get(Calendar.YEAR);
        int week = cal.get(Calendar.WEEK_OF_YEAR);
        String shard = "" + year + (week < 10 ? "0" + week : week);
        return shard;
    }

    /**
     * Computes weekly shard id for the current week..
     * 
     * @return
     */
    public static String shardWeekly() {
        return shardWeekly(0);
    }
}
