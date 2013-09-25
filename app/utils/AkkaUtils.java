package utils;

import java.util.concurrent.TimeUnit;

import play.libs.Akka;
import scala.concurrent.duration.Duration;
import scala.concurrent.duration.FiniteDuration;
import actors.BaseScheduledActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.Scheduler;

public class AkkaUtils {
    
    private static Scheduler scheduler = Akka.system().scheduler();
    
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
        ActorRef actorRef = Akka.system().actorOf(new Props(actorClass));
        FiniteDuration initialDuration = Duration.create(initialDelay, initialDelayTimeUnit);
        FiniteDuration repeatedDuration = Duration.create(repeatedDelay, repeatedDelayTimeUnit);
        Akka.system()
                .scheduler()
                .schedule(initialDuration, repeatedDuration, actorRef, "",
                        Akka.system().dispatcher());
    }
}
