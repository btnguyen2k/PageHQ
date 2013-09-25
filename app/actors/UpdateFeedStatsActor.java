package actors;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.ObjectUtils;

import play.Logger;
import play.Play;
import utils.Constants;
import utils.FacebookUtils;
import utils.JedisUtils;
import bo.FeedBo;
import bo.MyPagesDao;

import com.github.ddth.plommon.utils.DPathUtils;

/**
 * Akka actor: update active feeds' stats (num likes, shares, comments)
 * 
 * @author Thanh Nguyen
 */
public class UpdateFeedStatsActor extends BaseScheduledActor {

    @SuppressWarnings("unchecked")
    @Override
    protected void internalOnReceive(Object msg) throws Exception {
        Set<byte[]> activeFeeds = JedisUtils.setPop(Constants.REDIS_SET_ACTIVE_FEEDS, 10);
        int num = activeFeeds != null ? activeFeeds.size() : 0;
        Logger.debug("Num active feeds: " + num);
        if (num > 0) {
            for (byte[] rawData : activeFeeds) {
                Map<String, Object> data = JedisUtils.deserializes(rawData, Map.class);
                FeedBo feed = new FeedBo().fromMap(data);
                String appAccessToken = Play.application().configuration()
                        .getString(Constants.APPCONF_FB_APP_TOKEN);
                Map<String, Object> obj = FacebookUtils.getFeedInfo(feed.getFeedId(),
                        appAccessToken);
                Integer numShares = DPathUtils.getValue(obj, "shares.count", Integer.class);
                List<Object> likes = DPathUtils.getValue(obj, "likes.data", List.class);
                Integer numLikes = likes != null ? likes.size() : null;
                List<Object> comments = DPathUtils.getValue(obj, "comments.data", List.class);
                Integer numComments = comments != null ? comments.size() : null;
                feed = MyPagesDao.getFeed(feed.getId());
                if (feed != null) {
                    boolean isDirty = false;
                    if (!ObjectUtils.equals(feed.getNumComments(), numComments)) {
                        isDirty = true;
                        feed.setNumComments(numComments);
                    }
                    if (!ObjectUtils.equals(feed.getNumLikes(), numLikes)) {
                        isDirty = true;
                        feed.setNumLikes(numLikes);
                    }
                    if (!ObjectUtils.equals(feed.getNumShares(), numShares)) {
                        isDirty = true;
                        feed.setNumShares(numShares);
                    }
                    if (isDirty) {
                        feed = MyPagesDao.updateFeed(feed);
                        Logger.debug("\tActive feed [" + feed.getId() + "], type: "
                                + feed.getFeedType() + " / Likes [" + numLikes + "] / Shares ["
                                + numShares + "] / Comments [" + numComments + "]: DIRTY!");
                    } else {
                        Logger.debug("\tActive feed [" + feed.getId() + "], type: "
                                + feed.getFeedType() + " / Likes [" + numLikes + "] / Shares ["
                                + numShares + "] / Comments [" + numComments + "]");
                    }
                }
                // System.out.println(obj.getClass() + ": " + obj);
            }
        }
    }

}
