package actuators;

import com.agentfactory.logic.agent.Actuator;
import com.agentfactory.logic.lang.FOS;

/**
 *
 * @author  roregan
 */
public class LessThan extends Actuator {
    public boolean act(FOS action) {
        // get data to compare
        double x = Double.parseDouble(action.argAt(0).toString());
        double y = Double.parseDouble(action.argAt(1).toString());

        // now compare them
        if(x <= y) return true;
        else return false;
    }
}
