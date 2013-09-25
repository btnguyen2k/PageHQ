package actors;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import play.Logger;
import utils.Constants;
import utils.JedisUtils;
import utils.Utils;
import bo.FeedBo;
import bo.MyPagesDao;
import bo.PageBo;

/**
 * Akka actor: update list of active feeds from active pages.
 * 
 * @author Thanh Nguyen
 */
public class UpdateActiveFeedsActor extends BaseScheduledActor {

    @SuppressWarnings("unchecked")
    @Override
    protected void internalOnReceive(Object msg) throws Exception {
        Set<byte[]> activePages = JedisUtils.setPop(Constants.REDIS_SET_ACTIVE_PAGES, 10);
        int num = activePages != null ? activePages.size() : 0;
        Logger.debug("Num active pages: " + num);
        if (num > 0) {
            for (byte[] rawData : activePages) {
                Map<String, Object> data = JedisUtils.deserializes(rawData, Map.class);
                PageBo page = new PageBo().fromMap(data);
                Set<FeedBo> activeFeeds = new HashSet<FeedBo>();
                for (int i = -2; i <= 0; i++) {
                    String shardId = Utils.shardWeekly(i);
                    Set<FeedBo> feeds = MyPagesDao.getFeedsOfPage(page.getId(), shardId);
                    if (feeds != null) {
                        activeFeeds.addAll(feeds);
                    }
                }
                Logger.debug("\tPage " + page.getId() + ":\t" + activeFeeds.size());
                for (FeedBo feed : activeFeeds) {
                    byte[] temp = JedisUtils.serialize(feed.toMap());
                    JedisUtils.setAdd(Constants.REDIS_SET_ACTIVE_FEEDS, temp);
                }
            }
        }
    }

}
