package utils;

import java.util.concurrent.TimeUnit;

import play.libs.Akka;
import scala.concurrent.duration.Duration;
import scala.concurrent.duration.FiniteDuration;
import actors.BaseScheduledActor;
import akka.actor.ActorRef;
import akka.actor.Props;

public class AkkaUtils {

    /**
     * Schedules a job.
     * 
     * @param actorClass
     * @param initialDelay
     * @param initialDelayTimeUnit
     * @param repeatedDelay
     * @param repeatedDelayTimeUnit
     */
    public static void schedule(Class<? extends BaseScheduledActor> actorClass, long initialDelay,
            TimeUnit initialDelayTimeUnit, long repeatedDelay, TimeUnit repeatedDelayTimeUnit) {
        ActorRef actorRef = Akka.system().actorOf(Props.create(actorClass));
        FiniteDuration initialDuration = initialDelay != 0 ? Duration.create(initialDelay,
                initialDelayTimeUnit) : Duration.Zero();
        FiniteDuration repeatedDuration = repeatedDelay != 0 ? Duration.create(repeatedDelay,
                repeatedDelayTimeUnit) : Duration.Zero();
        Akka.system()
                .scheduler()
                .schedule(initialDuration, repeatedDuration, actorRef, "",
                        Akka.system().dispatcher(), null);
    }
}
