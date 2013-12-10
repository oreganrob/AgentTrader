

import com.agentfactory.afapl2.interpreter.AFAPL2ArchitectureFactory;
import com.agentfactory.platform.core.DuplicateAgentNameException;
import com.agentfactory.platform.core.IAgent;
import com.agentfactory.platform.core.NoSuchArchitectureException;
import com.agentfactory.platform.core.NoSuchServiceException;
import com.agentfactory.platform.impl.DefaultAgentPlatform;
import com.agentfactory.platform.impl.PlatformServiceManager;
import com.agentfactory.platform.impl.RoundRobinTimeSliceFixedScheduler;
import com.agentfactory.service.ams.AgentManagementService;
import com.agentfactory.service.mts.http.HTTPMessageTransportService;
import com.agentfactory.visualiser.Debugger;
import java.util.HashMap;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import services.TraderService;

/**
 * Example Main class that illustrates how to deploy a basic Agent Factory
 * application.
 *
 * @author rem
 */
public class Main {
    public static void main(String[] args) {
    
        // Create a new agent platform with a basic name and domain
        DefaultAgentPlatform platform = new DefaultAgentPlatform();
        platform.setName("AgentTrader");
        platform.setDomain("ucd.ie");

        // Install a scheduling algorithm for executing the agents
        platform.setScheduler(new RoundRobinTimeSliceFixedScheduler());

        // Install and register the AFAPL2 Architecture Factory:
        // This enables support for instantiating AFAPL2 agents (i.e. agents
        // whose source code is identified by a .agent extension)
        AFAPL2ArchitectureFactory factory = new AFAPL2ArchitectureFactory();
        Properties props = new Properties();
        props.setProperty("TIMESLICE", "100");
        factory.configure(props);
        platform.getArchitectureService().registerArchitectureFactory(factory);

        // create service manager
        PlatformServiceManager manager = ((PlatformServiceManager)
                platform.getPlatformServiceManager());
        try {
            // add a service to handle interactions with the Betfair API
            // logging in, getting markets, placing bets etc...
            manager.addService(TraderService.class, "TraderService");
            
            // add a service to facilitate communication
            // between agents
            props = new Properties();
            props.setProperty("port", "4444");
            manager.addService(HTTPMessageTransportService.class,
                    HTTPMessageTransportService.NAME, 0, props);
        }
        catch (NoSuchServiceException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }

        // Install and start the Agent Factory Debugger
        /*Debugger debugger = new Debugger();
        debugger.init(new HashMap<String, String>(), platform);
        debugger.start();*/

        // Get a reference to the Agent Management Service so that the default
        // agent community can be created...
        AgentManagementService ams =
                (AgentManagementService)
                platform.getPlatformServiceManager().getServiceByName(
                AgentManagementService.NAME);
        try {
            // market scanning agent
            IAgent agent = ams.createAgent("scanner", "agents/scanner.agent");
            agent.initialise("BELIEF(state(scanning))");

            // bet placement agent
            agent = ams.createAgent("trader", "agents/trader.agent");
            agent.initialise("BELIEF(state(scanning))");

            // balance refresh agent
            agent = ams.createAgent("account", "agents/balance.agent");
            agent.initialise("BELIEF(state(scanning))");

            // strategy agents
            agent = ams.createAgent("InRunning", "agents/InRunning.agent");
            agent = ams.createAgent("HorseRacing", "agents/HorseRacing.agent");
            agent = ams.createAgent("WeightOfMoney", "agents/WeightOfMoney.agent");

            // start the agents - use if no debugger!
            ams.resumeAgent("scanner");
            ams.resumeAgent("trader");
            ams.resumeAgent("account");
            ams.resumeAgent("InRunning");
            ams.resumeAgent("HorseRacing");
            ams.resumeAgent("WeightOfMoney");
        }
        catch (NoSuchArchitectureException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (DuplicateAgentNameException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch(Exception e) {
            System.out.println("Exception!");
        }
    }
}
