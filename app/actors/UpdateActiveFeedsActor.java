package actors;

import java.util.Map;
import java.util.Set;

import play.Logger;
import utils.Constants;
import utils.JedisUtils;
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
                Logger.debug("\t" + activePages);
                // String appAccessToken =
                // Play.application().configuration().getString("fb.appToken");
                // Facebook facebook =
                // FacebookUtils.getFacebook(appAccessToken);
                // Map<String, Object> obj =
                // facebook.fetchObject("563260077046816_601945469844943",
                // Map.class);
                // System.out.println(obj.getClass() + ": " + obj);
            }
        }
    }

}
