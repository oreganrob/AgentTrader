package actuators;

import com.agentfactory.logic.agent.Actuator;
import com.agentfactory.logic.lang.FOS;
import services.TraderService;

/**
 *
 * @author  roregan
 */
public class PlaceLay extends Actuator {
    public boolean act(FOS action) {

         // extract bet data
        String selectionName = action.argAt(0).toString();
        int selectionId = Integer.parseInt(action.argAt(1).toString());
        double layOdds = Double.parseDouble(action.argAt(2).toString());
        double layStake = Double.parseDouble(action.argAt(3).toString());
        
        // get handle on service to get list of current percepts
        TraderService service = (TraderService) this.getService("TraderService");

        // log
        service.gui().getGui().textArea().append(
            "Lay Requested: "+selectionName+","+selectionId+","+
            layOdds+","+layStake+"\n");

        // make sure we have a valid service handle
        if(service == null) {
            adoptBelief("BELIEF(unavailable(TraderService))");
            return false;
        }

        // if we do then proceed we refreshing the data
        String message = service.placeLay(selectionId, layOdds, layStake);

        // log
        service.gui().getGui().textArea().append(message);

        // adopt belief to keep refreshing market data
        adoptBelief("BELIEF(state(scanning))");

        return true;
    }
}
