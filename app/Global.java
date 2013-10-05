import java.util.concurrent.TimeUnit;

import play.Application;
import play.GlobalSettings;
import utils.AkkaUtils;
import actors.FeedDbTableActor;
import actors.UpdateActiveFeedsActor;
import actors.UpdateActivePagesActor;
import actors.UpdateFeedStatsActor;

public class Global extends GlobalSettings {

    @Override
    public void onStart(Application app) {
        BaseDao.init();

        super.onStart(app);

        AkkaUtils.schedule(FeedDbTableActor.class, 10, TimeUnit.SECONDS, 300, TimeUnit.SECONDS);

        AkkaUtils
                .schedule(UpdateActivePagesActor.class, 10, TimeUnit.SECONDS, 10, TimeUnit.SECONDS);

        AkkaUtils
                .schedule(UpdateActiveFeedsActor.class, 10, TimeUnit.SECONDS, 10, TimeUnit.SECONDS);

        AkkaUtils.schedule(UpdateFeedStatsActor.class, 10, TimeUnit.SECONDS, 10, TimeUnit.SECONDS);
    }

    @Override
    public void onStop(Application app) {
        super.onStop(app);
    }

}
