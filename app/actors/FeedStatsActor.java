package actors;

import java.util.Map;
import java.util.Set;

import org.springframework.social.facebook.api.Facebook;

import play.Logger;
import play.Play;
import utils.Constants;
import utils.FacebookUtils;
import utils.JedisUtils;
import akka.actor.UntypedActor;

/**
 * Automatically update feed's stats.
 * 
 * @author Thanh Nguyen
 */
public class FeedStatsActor extends UntypedActor {

    @SuppressWarnings("unchecked")
    @Override
    public void onReceive(Object msg) throws Exception {
        Set<byte[]> activeAccounts = JedisUtils.setMembers(Constants.REDIS_SET_ACTIVE_ACCOUNTS);
        int num = activeAccounts != null ? activeAccounts.size() : 0;
        Logger.debug("Num active accounts: " + num);
        if (num > 0) {
            for (byte[] data : activeAccounts) {
                
                
                String account = new String(data);
                Logger.debug("\t" + account);
                String appAccessToken = Play.application().configuration().getString("fb.appToken");
                Facebook facebook = FacebookUtils.getFacebook(appAccessToken);
                Map<String, Object> obj = facebook.fetchObject("563260077046816_601945469844943",
                        Map.class);
                System.out.println(obj.getClass() + ": " + obj);
            }
        }
    }

}
