package Betfair;
import Betfair.BFExchangeServiceStub.BetStatusEnum;
import Betfair.BFExchangeServiceStub.GetAccountFundsResp;
import Betfair.BFExchangeServiceStub.MUBet;
import Betfair.BFExchangeServiceStub.Market;
import Betfair.BFExchangeServiceStub.Runner;
import Betfair.BFGlobalServiceStub.BFEvent;
import Betfair.BFGlobalServiceStub.EventType;
import Betfair.BFGlobalServiceStub.MarketSummary;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import Betfair.ExchangeAPI.Exchange;
import Betfair.InflatedMarketPrices.InflatedPrice;
import Betfair.InflatedMarketPrices.InflatedRunner;



/**
 * Display utilities 
 */
public final class Display {

	// Prevent instantiation
	private Display() {
	}

	// Ask a question and get a response. response must be one of the possibilities
	public static int getChoiceAnswer(String question, String[] possibilities)
                throws IOException {
		while (true) {
			println(question);
			for (int i = 0; i < possibilities.length; i++) {
				println("  " + possibilities[i] +" ["+(i+1)+"]");
			}
			int choice = getIntAnswer("Selection:") - 1;
			if (choice >= 0 && choice < possibilities.length )
			{
				return choice;
			}
		}
	}

	// Ask a question and get a response as an integer
	public static int getIntAnswer(String question, int minValue, int maxValue)
                throws IOException {
		while (true) {
			int result = getIntAnswer(question);
			if (result >= minValue && result <= maxValue) {
				return result;
			}
		}
	}

	// Ask a question and get a response as an integer
	public static int getIntAnswer(String question) throws IOException {
		while (true) {
			String intAsString = getStringAnswer(question);
			try {
				return Integer.parseInt(intAsString);
			} catch (NumberFormatException e) {
				// Not an integer. Try again.
			}
		}
	}

	// Ask a question and get a response as an double
	public static double getDoubleAnswer(String question, boolean allowBlank)
                throws IOException {
		while (true) {
			String doubleAsString = getStringAnswer(question);
			if (allowBlank && doubleAsString.length() == 0) {
				return 0.0d;
			}
			try {
				return Double.parseDouble(doubleAsString);
			} catch (NumberFormatException e) {
				// Not an integer. Try again.
			}
		}
	}

	// Ask a question and get a response
	public static String getStringAnswer(String question) throws IOException {
		print(question + " ");
		return readLine();
	}

	// Print some an exception message to the output.
	public static void showException(String value, Exception e) {
		println(value +", Exception is");
		e.printStackTrace();
	}

	// Confirm the user really wants to do this action
	public static boolean confirm(String msg) throws IOException {
		print(msg +  "- Type \"yes\" to continue. ");
		String answer = readLine();
		return (answer.equalsIgnoreCase("yes"));
	}

	// Print some data to the output, appending a carriage return.
	public static void println(String value) {
		System.out.println(value);
		System.out.flush();
	}
	
	// Print some data to the output.
	public static void print(String value) {
		System.out.print(value);
		System.out.flush();
	}
	
	
	// Show the funds available for an account
	public static void showFunds(Exchange exch, GetAccountFundsResp funds) {
		// Display a subset of the account funds. More information is available
		// in the funds object, but not output by this message
		println("Account funds for the "+exch+" exchange:");
		println("   Balance        : "+funds.getBalance());
		println("   Available      : "+funds.getAvailBalance());
		println("   Credit Limit   : "+funds.getCreditLimit());
		println("   Betfair Points : "+funds.getCurrentBetfairPoints());
		println("   Exposure       : "+funds.getExposure());
		println("   Exposure Limit : "+funds.getExpoLimit());
		println("");
	}

	// Show the runners for the market specified
	public static void showMarket(Exchange exch, Market m, InflatedMarketPrices prices) {
		// Display a subset of the market information. More information is available
		// in the object, but not output at this point
		println("Market: "+m.getName()+"("+m.getMarketId()+") on the "+exch+" exchange:");
		println("   Start time     : "+m.getMarketTime().getTime());
		println("   Status         : "+m.getMarketStatus());
		println("   Location       : "+m.getCountryISO3());
		println("");

		println("Runners:");
		for (InflatedRunner r: prices.getRunners()) {
			Runner marketRunner = null;
			
			for (Runner mr: m.getRunners().getRunner()) {
				if (mr.getSelectionId() == r.getSelectionId()) {
					marketRunner = mr;
					break;
				}
			}
			String bestLay = "";
			if (r.getLayPrices().size() > 0) {
				InflatedPrice p = r.getLayPrices().get(0);
				bestLay = String.format("%,10.2f %s @ %,6.2f", 
                                        p.getAmountAvailable(), prices.getCurrency(),
                                        p.getPrice());
			}
			
			String bestBack = "";
			if (r.getBackPrices().size() > 0) {
				InflatedPrice p = r.getBackPrices().get(0);
				bestBack = String.format("%,10.2f %s @ %,6.2f", 
                                        p.getAmountAvailable(), prices.getCurrency(),
                                        p.getPrice());
			}
	
			println(String.format("%20s (%7d): Matched Amount: %,10.2f, Last Matched: %,6.2f, Best Back %s, Best Lay:%s"
					, marketRunner.getName(), r.getSelectionId(), 
                                        r.getTotalAmountMatched(), r.getLastPriceMatched(),
                                        bestBack, bestLay));
		}
		println("");
	}
	
	// Show the total number of calls to each API Method
	public static void showCombinedUsage(UsageMap usage) {
		println("API call volume:");
		for (String method: usage.getAllMethodsCalled()) {
			println(String.format("   %-25s: %d", method,
                                usage.getTotalCallsForMethod(method)));
		}
		
	}

	// show the number of calls to API mehtods, broken down over time.
	public static void showTimedUsage(UsageMap usage) {
		println("API call volume by time:");
		List<String> methods = usage.getAllMethodsCalled();
		Set<Date> timeBuckets = usage.getAllTimeBuckets();
		
		DateFormat df = new SimpleDateFormat("H:mm");
		// Show header line
		StringBuilder sb = new StringBuilder();
		sb.append(String.format("%-25s", "API Function"));
		for (Date bucket: timeBuckets) {
			sb.append(" | " + df.format(bucket));
		}
		println(sb.toString());
		for (int i = 0; i < sb.length(); i++) {
			print("-");
		}
		println("");
		
		for (String method: methods) {
			print(String.format("%-25s", method));
			for (Date bucket: timeBuckets) {
				print(String.format("%8d", usage.getMethodCallsForBucket(method, bucket)));
			}
			println("");			
		}
		
	}

	// Show the bets for the market specified
	public static void showBets(Market m, MUBet[] bets) {
		println(String.format("Current bets on %s market:", m.getName()));
		for (int i = 0; i < bets.length; i++) {
			MUBet b = bets[i];

			Runner marketRunner = null;
			for (Runner mr: m.getRunners().getRunner()) {
				if (mr.getSelectionId() == b.getSelectionId()) {
					marketRunner = mr;
					break;
				}
			}
			
			println(String.format("   %2d: %9s bet on %15s for %,6.2f @ %s (id = %d)",
						i+1,
						((b.getBetStatus()==BetStatusEnum.M) ? "matched" : "unmatched"),
						marketRunner.getName(),
						b.getSize(),
						b.getPrice(),
						b.getBetId()));
		}
		println("");
	}
	
	//  open up standard input and read a line of text from it.
	private static String readLine() throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		return br.readLine();
	}
	

	/////////////////////////////////////////////////////////////////////////////////
	// CONVERTER METHODS
	/////////////////////////////////////////////////////////////////////////////////
	
	public static int getChoiceAnswer(String question, EventType[] possibilities) throws IOException {
		String[] strings = new String[possibilities.length];
		for (int i = 0; i < possibilities.length; i++)
		{
			strings[i] = possibilities[i].getName();
		}
		return getChoiceAnswer(question, strings);
	}

	public static int getChoiceAnswer(String question, BFEvent[] events, MarketSummary[] markets) throws IOException {
		String[] strings = new String[events.length + markets.length];
		if (strings.length == 0) {
			// No options here
			return -1;
		}
		for (int i = 0; i < strings.length; i++)
		{
			if (i < events.length) {
				strings[i] = "E: " + events[i].getEventName();
			} else {
				MarketSummary mkt = markets[i-events.length];
				
				strings[i] = "M: " +
							((mkt.getEventTypeId() == 13) ?
									String.format("%02d:%02d ", 
											mkt.getStartTime().get(Calendar.HOUR_OF_DAY), 
											mkt.getStartTime().get(Calendar.MINUTE))
								: "") 
								+ mkt.getMarketName();
			}
		}
		return getChoiceAnswer(question, strings);
	}
	
	public static int getChoiceAnswer(String question, Runner[] possibilities) throws IOException {
		String[] strings = new String[possibilities.length];
		for (int i = 0; i < possibilities.length; i++)
		{
			strings[i] = possibilities[i].getName();
		}
		return getChoiceAnswer(question, strings);
	}

}
