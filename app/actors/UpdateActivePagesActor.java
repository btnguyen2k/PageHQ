package actors;

import java.util.Set;

import play.Logger;
import utils.Constants;
import utils.JedisUtils;
import bo.MyPagesDao;
import bo.PageBo;

/**
 * Akka actor: update list of active pages from active users.
 * 
 * @author Thanh Nguyen
 */
public class UpdateActivePagesActor extends BaseScheduledActor {

    @Override
    protected void internalOnReceive(Object msg) throws Exception {
        Set<byte[]> activeAccounts = JedisUtils.setMembers(Constants.REDIS_SET_ACTIVE_ACCOUNTS);
        int num = activeAccounts != null ? activeAccounts.size() : 0;
        Logger.debug("Num active accounts: " + num);
        if (num > 0) {
            for (byte[] data : activeAccounts) {
                //JedisUtils.setRemove(Constants.REDIS_SET_ACTIVE_ACCOUNTS, data);

                String account = JedisUtils.deserializes(data, String.class);
                Logger.debug("\t" + account);
                PageBo[] pages = MyPagesDao.getPages(account);
                if (pages != null) {
                    Logger.debug("\t" + pages.length);
                    for (PageBo page : pages) {
                        byte[] temp = JedisUtils.serialize(page.toMap());
                        JedisUtils.setAdd(Constants.REDIS_SET_ACTIVE_PAGES, temp);
                    }
                }
            }
        }
    }

}
