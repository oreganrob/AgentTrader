package modules;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

 public class PlacedBets extends com.agentfactory.logic.agent.Module {
   //private Vector PlacedBets;
   private Map<Integer, String> PlacedBets;

   @Override
   public void init() {
     //PlacedBets = new Vector();
     PlacedBets = new HashMap<Integer, String>();
   }

   public void addPlacedBet(int selectionId, String betDetails) {
     //PlacedBets.add(betId);
     PlacedBets.put(selectionId, betDetails);
   }

   public int numPlacedBets() {
     return PlacedBets.size();
   }
 }
