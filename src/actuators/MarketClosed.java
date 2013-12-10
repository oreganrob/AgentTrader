package actuators;

import com.agentfactory.logic.agent.Actuator;
import com.agentfactory.logic.lang.FOS;

/**
 *
 * @author  roregan
 */
public class MarketClosed extends Actuator {
    public boolean act(FOS action) {
        System.out.println("Market Closed!");
        return true;
    }
}
