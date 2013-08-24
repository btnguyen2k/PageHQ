import java.util.Calendar;

import play.Application;
import play.GlobalSettings;
import akka.actor.UntypedActor;

public class Global extends GlobalSettings {

    static class MyActor extends UntypedActor {
        @Override
        public void onReceive(Object msg) throws Exception {
            // System.out.println(msg);
        }
    }

    @Override
    public void onStart(Application app) {
        super.onStart(app);
        // System.out.println("Start: " + app);
        // ActorRef myActor = Akka.system().actorOf(new Props(MyActor.class));
        // Akka.system()
        // .scheduler()
        // .schedule(Duration.create(0, TimeUnit.MILLISECONDS),
        // Duration.create(10, TimeUnit.SECONDS), myActor, "tick",
        // Akka.system().dispatcher());
    }

    @Override
    public void onStop(Application app) {
        super.onStop(app);
        // System.out.println("Stop: " + app);
    }

    public static void main(String[] args) {
        Calendar cal = Calendar.getInstance();
        int weeks = cal.get(Calendar.WEEK_OF_YEAR);
        System.out.println(weeks);
    }
}
