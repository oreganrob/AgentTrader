package actuators;

import com.agentfactory.logic.agent.Actuator;
import com.agentfactory.logic.lang.FOS;
import services.TraderService;

/**
 *
 * @author  roregan
 */
public class AccountRefresh extends Actuator {
    public boolean act(FOS action) {
         // get handle on service to get list of current percepts
        TraderService service = (TraderService) this.getService("TraderService");

        // make sure we have a valid service handle
        if(service == null) {
            adoptBelief("BELIEF(unavailable(TraderService))");
            return false;
        }

        // if we do then proceed we refreshing the data
        service.refreshAccountBalance();

        // adopt belief to keep refreshing market data
        adoptBelief("BELIEF(state(scanning))");

        return true;
    }
}
