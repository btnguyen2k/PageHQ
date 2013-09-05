package utils;

import java.util.Calendar;

/**
 * Misc utilities.
 * 
 * @author Thanh.Nguyen <btnguyen2k@gmail.com>
 */
public class Utils {
    /**
     * Computes weekly shard.
     * 
     * @return
     */
    public static String shardWeekly() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int week = cal.get(Calendar.WEEK_OF_YEAR);
        String shard = "" + year + (week < 10 ? "0" + week : week);
        return shard;
    }
}
