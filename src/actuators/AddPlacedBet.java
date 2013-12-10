package actuators;

import com.agentfactory.logic.agent.Actuator;
import com.agentfactory.logic.lang.FOS;
import modules.PlacedBets;

/**
 *
 * @author  roregan
 */
public class AddPlacedBet extends Actuator {
    public boolean act(FOS action) {

        // extract bet data
        int selectionId = Integer.parseInt(action.argAt(0).toString());
        double odds = Double.parseDouble(action.argAt(1).toString());
        double stake = Double.parseDouble(action.argAt(2).toString());

        PlacedBets placedBets = (PlacedBets) this.getModuleByName("PlacedBets");
        if (placedBets == null) {
            adoptBelief("BELIEF(noPlacedBetsModule())");
            return false;
        }
        else {
            placedBets.addPlacedBet(selectionId, odds+","+stake);
            adoptBelief("BELIEF(addedBet(" + selectionId + "))");
        }
        return true;
    }
}
