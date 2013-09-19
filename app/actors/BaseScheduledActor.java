package actors;

import akka.actor.UntypedActor;

public abstract class BaseScheduledActor extends UntypedActor {

    /**
     * {@inheritDoc}
     */
    @Override
    public void onReceive(Object message) throws Exception {
        internalOnReceive(message);
    }

    /**
     * Sub-class overrides this method to implement its own business.
     * 
     * @param message
     * @throws Exception
     */
    protected abstract void internalOnReceive(Object message) throws Exception;

}
