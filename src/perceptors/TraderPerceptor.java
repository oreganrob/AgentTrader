package perceptors;

import com.agentfactory.logic.agent.Perceptor;

/**
 *
 * @author  roregan
 */
public class TraderPerceptor extends Perceptor {
    
    public void perceive() {
        // adopt belief of trader agent location
        adoptBelief("BELIEF(tradingAgent(trader, addresses(http://localhost:4444/acc)))");

        // adopt belief of balance agent location
        adoptBelief("BELIEF(balanceAgent(balance, addresses(http://localhost:4444/acc)))");
    }
}
