package perceptors;

import com.agentfactory.logic.agent.Perceptor;
import com.agentfactory.logic.lang.FOS;
import services.TraderService;

/**
 *
 * @author  roregan
 */
public class BalancePerceptor extends Perceptor {
    
    public void perceive() {
        // get handle on service to get list of current percepts
        TraderService service = (TraderService) this.getService("TraderService");

        if(service == null) {
            adoptBelief("BELIEF(unavailable(TraderService))");
            return;
        }

        // else get list of percepts and adopt them
        for (FOS fos: service.getAccountPercepts(agent)) {
            adoptBelief("BELIEF("+fos.toString()+")");
        }
    }
}
