package perceptors;

import com.agentfactory.logic.agent.Perceptor;
import modules.PlacedBets;

/**
 *
 * @author  roregan
 */
public class PlacedBetsPerceptor extends Perceptor {
    
    public void perceive() {
        //int size = 0;
        //Queue queue = null;

        PlacedBets placedBets = (PlacedBets) this.getModuleByName("PlacedBets");
        adoptBelief("BELIEF(placedBets(" + placedBets.numPlacedBets() + "))");

        /*List queues = this.getModulesByClass(QUEUE_CLASS);
        Iterator it = queues.iterator();
        while (it.hasNext()) {
            queue = (Queue) it.next();
            size = queue.size();
            adoptBelief("BELIEF(queueSize(" + queue.getName() + "," + size + "))");
            if (size > 0) {
                adoptBelief("BELIEF(queueHead(" + queue.getName() + "," + queue.head() + "))");
            }
        }*/
    }
}
