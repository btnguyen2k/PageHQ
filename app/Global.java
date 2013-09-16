import java.util.concurrent.TimeUnit;

import play.Application;
import play.GlobalSettings;
import play.libs.Akka;
import scala.concurrent.duration.Duration;
import actors.FeedDbTableActor;
import actors.FeedStatsActor;
import akka.actor.ActorRef;
import akka.actor.Props;

import com.github.ddth.plommon.bo.BaseDao;

public class Global extends GlobalSettings {

    @Override
    public void onStart(Application app) {
        BaseDao.init();

        super.onStart(app);
        ActorRef feedDbTableACtor = Akka.system().actorOf(new Props(FeedDbTableActor.class));
        Akka.system()
                .scheduler()
                .schedule(Duration.create(10, TimeUnit.SECONDS),
                        Duration.create(300, TimeUnit.SECONDS), feedDbTableACtor, "",
                        Akka.system().dispatcher());

        ActorRef feedStatsActor = Akka.system().actorOf(new Props(FeedStatsActor.class));
        Akka.system()
                .scheduler()
                .schedule(Duration.create(10, TimeUnit.SECONDS),
                        Duration.create(10, TimeUnit.SECONDS), feedStatsActor, "",
                        Akka.system().dispatcher());
    }

    @Override
    public void onStop(Application app) {
        super.onStop(app);
    }

}
