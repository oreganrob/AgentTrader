package Betfair;

import Betfair.BFExchangeServiceStub.BetCategoryTypeEnum;
import Betfair.BFExchangeServiceStub.BetPersistenceTypeEnum;
import Betfair.BFExchangeServiceStub.BetTypeEnum;
import Betfair.BFExchangeServiceStub.CancelBets;
import Betfair.BFExchangeServiceStub.CancelBetsResult;
import Betfair.BFExchangeServiceStub.GetAccountFundsResp;
import Betfair.BFExchangeServiceStub.MUBet;
import Betfair.BFExchangeServiceStub.Market;
import Betfair.BFExchangeServiceStub.PlaceBets;
import Betfair.BFExchangeServiceStub.PlaceBetsResult;
import Betfair.BFExchangeServiceStub.Runner;
import Betfair.BFExchangeServiceStub.UpdateBets;
import Betfair.BFExchangeServiceStub.UpdateBetsResult;
import Betfair.BFGlobalServiceStub.BFEvent;
import Betfair.BFGlobalServiceStub.EventType;
import Betfair.BFGlobalServiceStub.GetEventsResp;
import Betfair.BFGlobalServiceStub.MarketSummary;
import Betfair.ExchangeAPI.Exchange;

/** 
 * Demonstration of the Betfair API.
 * 
 * This is the main control class for running the Betfair API demo. 
 * User display and input is handled by the Display class
 * API Management is handled by the classes in the apihandler package 
 */ 
public class BetfairAPI
{
	// Menus
	private static final String[] MAIN_MENU = new String[] 
	    {"View account", "Choose Market", "View Market",
             "Bet Management", "View Usage", "Exit"};
	   
	private static final String[] BETS_MENU = new String[] 
 	    {"Place Bet", "Update Bet", "Cancel Bet", "Back"};

	// The session token
	private static APIContext apiContext = new APIContext();
	
	// the current chosen market and Exchange for that market
	private static Market selectedMarket;
	private static Exchange selectedExchange;

        // entered details
        String username;
        String password;
        int marketId;
	
	// Fire up the API demo
        public void login(String username, String password, String marketId) {
                //Display.println("Welcome to AgentTrader");
		//Display.println("Betfair Login:");

                this.username = username;
                this.password = password;
                this.marketId = Integer.parseInt(marketId);
               
		// Perform the login before anything else.
		try {
                        // get username and password from user
                        //String username = Display.getStringAnswer("Username: ");
                        //String password = Display.getStringAnswer("Password: ");

                        // get the id of the market to scan
                        //int marketId = Display.getIntAnswer("Market: ");

                        //
                        //Display.println("Please Wait...");

                        // login
			GlobalAPI.login(apiContext, this.username, this.password);

                        // Exchange ID of 1 is the UK, 2 is AUS
                        selectedExchange = Exchange.UK;
                        selectedMarket = ExchangeAPI.getMarket(
                                selectedExchange,
                                apiContext, this.marketId);
		}
		catch (Exception e) {
			// If we can't log in for any reason, just exit.
			Display.showException("Problem Logging In", e);
			System.exit(1);
		}
	}

	// Check if a market is selected
	public boolean isMarketSelected() {
		if (selectedMarket == null) {
			Display.println("You must Select a Market");
			return false;
		}
		return true;
	}

	// Retrieve and display the account funds for the specified exchange
	public void showAccountFunds(Exchange exch) throws Exception {
		GetAccountFundsResp funds = ExchangeAPI.getAccountFunds(exch, apiContext);
		Display.showFunds(exch, funds);
	}
	
	// Select a market by the following process
	// * Select a type of event
	// * Recursively select an event of this type
	// * Select a market within this event if one exists.
	public void chooseMarket() throws Exception {
		// Get available event types.
		//EventType[] types = GlobalAPI.getActiveEventTypes(apiContext);
		//int typeChoice = Display.getChoiceAnswer("Choose an event type:", types);

		// Get available events of this type
		/*selectedMarket = null;
		int eventId = types[typeChoice].getId();
		while (selectedMarket == null) {
			GetEventsResp resp = GlobalAPI.getEvents(apiContext, eventId);
			
			// An event can have both markets and sub-events
			BFEvent[] events = resp.getEventItems().getBFEvent() == null 
				? new BFEvent[0] : resp.getEventItems().getBFEvent();
			MarketSummary[] markets = resp.getMarketItems().getMarketSummary() == null 
				? new MarketSummary[0] : resp.getMarketItems().getMarketSummary();

			int choice = Display.getChoiceAnswer("Choose an event or market:", events, markets);
			if (choice == -1) {
				// Probably a coupon event, which are not supported in this demo.				Display.println("No choices available - returning to root");
				eventId = types[typeChoice].getId();
			}
                        else if (choice < events.length) {
				// Always display events before markets,
                                // so if it's less that events.length, it's an event.
				eventId = events[choice].getEventId();
			}
                        else {
				// A market. Select it and drop out of the loop
				int marketChoice = choice - events.length;

				// Exchange ID of 1 is the UK, 2 is AUS
				selectedExchange = markets[marketChoice].getExchangeId() == 1 ?
                                    Exchange.UK : Exchange.AUS;
				selectedMarket = ExchangeAPI.getMarket(selectedExchange,
                                        apiContext, markets[marketChoice].getMarketId());
			}
		}*/
	}
	
	// Retrieve and view information about the selected market
	public void viewMarket() throws Exception {            
            if (isMarketSelected()) {
                    InflatedMarketPrices prices = ExchangeAPI.getMarketPrices(
                            selectedExchange, apiContext, selectedMarket.getMarketId());

                    // Now show the inflated compressed market prices.
                    Display.showMarket(selectedExchange, selectedMarket, prices);
            }
	}

        // returns live market prices - i.e. odds
	public InflatedMarketPrices getMarketPrices() throws Exception {
            if (isMarketSelected()) {
                InflatedMarketPrices prices = ExchangeAPI.getMarketPrices(
                    selectedExchange, apiContext, selectedMarket.getMarketId());

                return prices;
            }

            return null;
	}

        // returns market details
	public Market getMarketDetails() throws Exception {
            if (isMarketSelected()) {
                return selectedMarket;
            }

            return null;
	}

        // returns the account available to bet balance
        public double getAccountBalance() throws Exception {
            // request the live account data
            GetAccountFundsResp funds = ExchangeAPI.getAccountFunds(
                    Exchange.UK, apiContext);

            // Display a subset of the account funds. More information is available
            // in the funds object, but not output by this message
            /*println("   Balance        : "+funds.getBalance());
            println("   Available      : "+funds.getAvailBalance());
            println("   Credit Limit   : "+funds.getCreditLimit());
            println("   Betfair Points : "+funds.getCurrentBetfairPoints());
            println("   Exposure       : "+funds.getExposure());
            println("   Exposure Limit : "+funds.getExpoLimit());
            println("");*/

            return funds.getAvailBalance();
        }

        // Place a bet on the market.
	public String placeBack(int selectionId,
                double odds, double stake) throws Exception {

                // default message;
                String message = "Problem";

		if (isMarketSelected()) {
			//Runner[] runners = selectedMarket.getRunners().getRunner();
			//int choice = Display.getChoiceAnswer("Choose a Runner:", runners);

			// Set up the individual bet to be placed
			PlaceBets bet = new PlaceBets();
			bet.setMarketId(selectedMarket.getMarketId());
			bet.setSelectionId(selectionId);
			bet.setBetCategoryType(BetCategoryTypeEnum.E);
			bet.setBetPersistenceType(BetPersistenceTypeEnum.NONE);
			bet.setBetType(BetTypeEnum.B);
			bet.setPrice(odds);
			bet.setSize(stake);

                        // send request
                        PlaceBetsResult betResult = ExchangeAPI.placeBets(
                                selectedExchange, apiContext,
                                new PlaceBets[] {bet})[0];

                        if (betResult.getSuccess()) {
                                message = "Back "+betResult.getBetId()+
                                        " placed. "+betResult.getSizeMatched() +
                                        " matched @ "+betResult.getAveragePriceMatched();
                        } else {
                                message = "Failed to Place Back: Problem was: "+
                                        betResult.getResultCode();
                        }
		}

                return message;
	}

        // Place a bet on the market.
	public String placeLay(int selectionId,
                double odds, double stake) throws Exception {

                // default message;
                String message = "Problem";

		if (isMarketSelected()) {
			//Runner[] runners = selectedMarket.getRunners().getRunner();
			//int choice = Display.getChoiceAnswer("Choose a Runner:", runners);

			// Set up the individual bet to be placed
			PlaceBets bet = new PlaceBets();
			bet.setMarketId(selectedMarket.getMarketId());
			bet.setSelectionId(selectionId);
			bet.setBetCategoryType(BetCategoryTypeEnum.E);
			bet.setBetPersistenceType(BetPersistenceTypeEnum.NONE);
			bet.setBetType(BetTypeEnum.L);
			bet.setPrice(odds);
			bet.setSize(stake);

                        // send request
                        PlaceBetsResult betResult = ExchangeAPI.placeBets(
                                selectedExchange, apiContext,
                                new PlaceBets[] {bet})[0];

                        if (betResult.getSuccess()) {
                                message = "Lay "+betResult.getBetId()+
                                        " placed. "+betResult.getSizeMatched() +
                                        " matched @ "+betResult.getAveragePriceMatched();
                        } else {
                                message = "Failed to Place Lay: Problem was: "+
                                        betResult.getResultCode();
                        }
		}

                return message;
	}

	// show all my matched and unmatched bets specified market.
	public void manageBets() throws Exception {
		if (isMarketSelected()) {
			boolean finished = false;
			while (!finished) {
				// show current bets
				MUBet[] bets = ExchangeAPI.getMUBets(
                                        selectedExchange, apiContext,
                                        selectedMarket.getMarketId());
				Display.showBets(selectedMarket, bets);
				
				int choice = Display.getChoiceAnswer(
                                        "Choose an operation", BETS_MENU);
				switch (choice) {
					case 0: // Place Bet
						placeBet();
						break;
					case 1: // Update Bet
						updateBet(bets[Display.getIntAnswer(
                                                        "Choose a bet:", 1,
                                                        bets.length) - 1]);
						break;
					case 2: // Cancel Bet
						cancelBet(bets[Display.getIntAnswer(
                                                        "Choose a bet:", 1,
                                                        bets.length) - 1]);
						break;
					case 3: // Back
						finished = true;
						break;
				}
			}
		}
	}
	
	// Place a bet on the specified market.
	public void placeBet() throws Exception {
		if (isMarketSelected()) {
			Runner[] runners = selectedMarket.getRunners().getRunner();
			int choice = Display.getChoiceAnswer("Choose a Runner:", runners);
			
			// Set up the individual bet to be placed
			PlaceBets bet = new PlaceBets();
			bet.setMarketId(selectedMarket.getMarketId());
			bet.setSelectionId(runners[choice].getSelectionId());
			bet.setBetCategoryType(BetCategoryTypeEnum.E);
			bet.setBetPersistenceType(BetPersistenceTypeEnum.NONE);
			bet.setBetType(BetTypeEnum.Factory.fromValue(
                                Display.getStringAnswer("Bet type:")));
			bet.setPrice(Display.getDoubleAnswer("Price:", false));
			bet.setSize(Display.getDoubleAnswer("Size:", false));
			
			if (Display.confirm("This action will actually "+
                                "place a bet on the Betfair exchange")) {
				// We can ignore the array here as we only sent in one bet.
				PlaceBetsResult betResult = ExchangeAPI.placeBets(
                                        selectedExchange, apiContext, new PlaceBets[] {bet})[0];
				
				if (betResult.getSuccess()) {
					Display.println("Bet "+betResult.getBetId()+
                                                " placed. "+betResult.getSizeMatched() +
                                                " matched @ "+betResult.getAveragePriceMatched());
				} else {
					Display.println("Failed to place bet: Problem was: "+
                                                betResult.getResultCode());
				}
			}
		}
	}
	
	// Place a bet on the specified market.
	public void updateBet(MUBet bet) throws Exception {
		if (isMarketSelected()) {
			double newPrice = Display.getDoubleAnswer("New Price [Unchanged - "+
                                bet.getPrice()+"]:", true);
			double newSize = Display.getDoubleAnswer("New Size [Unchanged - "+
                                bet.getSize()+"]:", true);

			if (newPrice == 0.0d) {
				newPrice = bet.getPrice();
			}
			if (newSize == 0.0d) {
				newSize = bet.getSize();
			}

			// Set up the individual bet to be edited
			UpdateBets upd = new UpdateBets(); 
			upd.setBetId(bet.getBetId());
			upd.setOldBetPersistenceType(bet.getBetPersistenceType());
			upd.setOldPrice(bet.getPrice());
			upd.setOldSize(bet.getSize());
			upd.setNewBetPersistenceType(bet.getBetPersistenceType());
			upd.setNewPrice(newPrice);
			upd.setNewSize(newSize);
			
			if (Display.confirm("This action will actually edit a bet on the Betfair exchange")) {
				// We can ignore the array here as we only sent in one bet.
				UpdateBetsResult betResult = ExchangeAPI.updateBets(
                                        selectedExchange, apiContext, new UpdateBets[] {upd})[0];
				
				if (betResult.getSuccess()) {
					Display.println("Bet "+betResult.getBetId()+
                                                " updated. New bet is "+betResult.getNewSize() +
                                                " @ "+betResult.getNewPrice());
				} else {
					Display.println("Failed to update bet: Problem was: "+
                                                betResult.getResultCode());
				}
			}
		}
	}
	
	// Place a bet on the specified market.
	public void cancelBet(MUBet bet) throws Exception {
		if (isMarketSelected()) {
			if (Display.confirm("This action will actually cancel a bet on the Betfair exchange")) {
				CancelBets canc = new CancelBets();
				canc.setBetId(bet.getBetId());
				
				// We can ignore the array here as we only sent in one bet.
				CancelBetsResult betResult = ExchangeAPI.cancelBets(
                                        selectedExchange, apiContext, new CancelBets[] {canc})[0];
				
				if (betResult.getSuccess()) {
					Display.println("Bet "+betResult.getBetId()+" cancelled.");
				} else {
					Display.println("Failed to cancel bet: Problem was: "+
                                                betResult.getResultCode());
				}
			}
		}
	}
}
