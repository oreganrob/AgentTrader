package services;

import com.agentfactory.logic.lang.FOS;
import com.agentfactory.platform.core.IAgent;
/*import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;*/
import Betfair.BetfairAPI;
import Betfair.InflatedMarketPrices;
import Betfair.BFExchangeServiceStub.Market;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import Betfair.InflatedMarketPrices.InflatedPrice;
import Betfair.InflatedMarketPrices.InflatedRunner;
import Betfair.BFExchangeServiceStub.Runner;
import GUI.GUI;
import java.util.Calendar;

public class TraderService
        extends com.agentfactory.platform.impl.AbstractPlatformService
{
    protected BetfairAPI api;
    protected Map<String, List<FOS>> mapAgentToPercepts = new HashMap<String, List<FOS>>();
    protected InflatedMarketPrices prices;
    protected Market market;
    protected double balance;
    protected GUI gui;
    
    @Override
    public void onStart() {

        try {
            // create GUI for logging
            gui = new GUI();
            gui.create();
            gui.getGui().textArea().append("Welcome to AgentTrader\n");

            // poll for user hitting go
            // very poor solution but will do
            // TODO implement as listener
            while(!gui.getGui().proceed())            
                Thread.sleep(1000);
            
            // create API instance
            api = new BetfairAPI();

            // login through API
            api.login(gui.getGui().username(), gui.getGui().password(),
                    gui.getGui().marketId());
        }
        catch(Exception e) {
            System.out.println("Exception!");
        }
    }

    public void onBind(IAgent agent) {
        System.out.println("Agent: '"+agent.getName()+"' Bound to Service");
        mapAgentToPercepts.put(agent.getName(), new LinkedList<FOS>());
    }

    public GUI gui() {
        return gui;
    }

     /**
     * create the percepts for an agent
     * @param agent
     */
    public void updateMarketPercepts(String agent) {
        List<FOS> percepts = mapAgentToPercepts.get(agent);
    	percepts.clear();

        try {
            // work out how far it is to the start/off in seconds
            long startTime = (market.getMarketSuspendTime().getTimeInMillis()/1000);
            long currentTime = (Calendar.getInstance().getTimeInMillis()/1000);
            long secondsToOff = startTime - currentTime;

            // add percepts for market information            
            percepts.add(new FOS("marketName("+market.getName()+")"));
            percepts.add(new FOS("marketId("+market.getMarketId()+")"));
            percepts.add(new FOS("marketLocale("+ market.getCountryISO3()+")"));
            percepts.add(new FOS("secondsToOff("+secondsToOff+")"));
            percepts.add(new FOS("marketStatus("+
                    prices.getMarketStatus()+")"));
            percepts.add(new FOS("marketPlaces("+
                    market.getNumberOfWinners()+")"));
            percepts.add(new FOS("marketType("+
                    market.getMarketType().toString()+")"));
            percepts.add(new FOS("marketSportId("+
                    market.getEventTypeId()+")"));            

            // in-running?
            if(prices.getInPlayDelay() > 0) {
                percepts.add(new FOS("marketInRunning(yes)"));
            }
            else {
                percepts.add(new FOS("marketInRunning(no)"));
            }
           
            // create variable to add up how much is matched 
            // on each selection to give us a total amount matched on market
            double marketMatched = 0;

            // add percepts for eachs selection/runners available odds & stakes
            for (InflatedRunner r: prices.getRunners()) {
                Runner marketRunner = null;

                // find the runner in the market data object
                for (Runner mr: market.getRunners().getRunner()) {
                    if (mr.getSelectionId() == r.getSelectionId()) {
                        marketRunner = mr;
                        break;
                    }
                }

                // best lay price
                float bestLayOdds = 0;
                float bestLayStake = 0;
                if (r.getLayPrices().size() > 0) {
                        InflatedPrice p = r.getLayPrices().get(0);
                        bestLayOdds = (float)p.getPrice();
                        bestLayStake = (float)p.getAmountAvailable();
                }

                // best back price
                float bestBackOdds = 0;
                float bestBackStake = 0;
                if (r.getBackPrices().size() > 0) {
                        InflatedPrice p = r.getBackPrices().get(0);
                        bestBackOdds = (float)p.getPrice();
                        bestBackStake = (float)p.getAmountAvailable();
                }

                // create percepts for each selection/runner
                percepts.add(new FOS("selectionMatched("+marketRunner.getName()+","+
                        r.getTotalAmountMatched()+")"));
                percepts.add(new FOS("bestBack("+marketRunner.getName()+
                        ","+r.getSelectionId()+","+bestBackOdds+","+
                        bestBackStake+")"));
                percepts.add(new FOS("bestLay("+marketRunner.getName()+
                        ","+r.getSelectionId()+","+bestLayOdds+","+
                        bestLayStake+")"));

                // create a weight of money belief for the selection
                float wom = (bestBackStake/(bestBackStake+bestLayStake)*100);
                percepts.add(new FOS("wom("+marketRunner.getName()+
                        ","+r.getSelectionId()+","+bestBackOdds+","+
                        bestLayOdds+","+wom+")"));

                // add selection matched to market matched
                marketMatched +=  r.getTotalAmountMatched();
            }

            // create percept for total market matched
            percepts.add(new FOS("marketMatched("+marketMatched+")"));
        }
        catch(Exception e) {
            System.out.println("Exception!");
        }
    }

    /**
     *
     * @param agent
     */
    public void updateAccountPercepts(String agent) {
        List<FOS> percepts = mapAgentToPercepts.get(agent);
    	percepts.clear();

        try {
            // add percepts for account information
            percepts.add(new FOS("availableBalance("+balance+")"));
        }
        catch(Exception e) {
            System.out.println("Exception!");
        }
    }

    /**
     *
     * @param agent
     * @return
     */
    public List<FOS> getMarketPercepts(IAgent agent) {
        // update the percepts first
        updateMarketPercepts(agent.getName());

        // then return them
        return mapAgentToPercepts.get(agent.getName());
    }

    /**
     * 
     * @param agent
     * @return
     */
    public List<FOS> getAccountPercepts(IAgent agent) {
        // update the percepts first
        updateAccountPercepts(agent.getName());

        // then return them
        return mapAgentToPercepts.get(agent.getName());
    }
    
    /**
     *
     */
    public void refreshMarketData() {
        gui.getGui().textArea().append("Refreshing Market Data...\n");
        try {
            // get markets details
            market = api.getMarketDetails();
            prices = api.getMarketPrices();
        }
        catch(Exception e) {
            System.out.println("Exception Refreshing Market Data");
        }
    }

    /**
     * 
     */
    public void refreshAccountBalance() {
        gui.getGui().textArea().append("Refreshing Account Data...\n");
        //System.out.println("Refreshing Account Data...");
        try {
            // get markets details
            balance = api.getAccountBalance();            
        }
        catch(Exception e) {
            System.out.println("Exception Refreshing Account Data");
        }
    }

    /**
     * place a back bet
     */
    public String placeBack(int selectionId,
            double odds, double stake) {
        String message  = "";

        gui.getGui().textArea().append("Placing Back Bet\n");
        try {
            // get markets details
            message = api.placeBack(selectionId, odds, stake);
        }
        catch(Exception e) {
            System.out.println("Exception Placing Back");
        }

        return message;
    }

    /**
     * place a lay bet
     */
    public String placeLay(int selectionId,
            double odds, double stake) {
        String message  = "";

        gui.getGui().textArea().append("Placing Lay Bet\n");
        try {
            // get markets details
            message = api.placeLay(selectionId, odds, stake);
        }
        catch(Exception e) {
            System.out.println("Exception Placing Lay");
        }
        
        return message;
    }
}