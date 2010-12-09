package rps.statistics;

import agent.Agent;
import agent.client.BasicClient;
import java.io.IOException;
import java.net.InetAddress;
import java.util.List;

/**
 * Client sending out agents for gathering statistics
 * @author Daniel Abrahamsson
 */
public class StatisticsClient extends BasicClient {

    /**
     * Instantiates the client. The client will use
     * the opportunity to find a service point.
     * @param groupAddress Multicast address to use for service point discovery.
     * @param basePort Multicast port to use for service point discovery.
     * @param clientPort The port used to receive agents.
     * @throws IOException If peer discovery failed.
     */
    public StatisticsClient(InetAddress groupAddress, int basePort, int clientPort) throws IOException {
        super(groupAddress, basePort, clientPort);
    }

    /**
     * Returns a list of agents to be sent to the server.
     * @return a list of agents to be sent to the server.
     */
    @Override
    protected List<Agent> initAgents() {
        List<Agent> agents = new java.util.LinkedList<Agent>();
        agents.add(new StatisticsAgent(getClientAddress(), clientPort));
        return agents;
    }

    /**
     * Called when the client receives an agent from a server.
     * @param agent The newly retrieved agent
     */
    @Override
    protected void onAgentReceived(Agent agent) {
        StatisticsAgent ss = (StatisticsAgent)agent;
        ss.printStatistics();
    }

    /**
     * Called if an agent could not be delivered to the service point.
     * @param agent The agent that we failed to deliver.
     * @param reason The reason of the error.
     */
    @Override
    protected void onDeliveryError(Agent agent, Exception reason) {
        System.err.println("Failed to deliver agent: " + reason);
    }

     /**
     * Called when we failed to receive an agent.
     * @param reason The reason of the error
     */
    @Override
    protected void onReceiveError(Exception reason) {
        System.err.println("Failed to receive agent: " + reason);
    }

    /**
     * Test-method
     * @param args [0] client port, [1] multicast ip, [2] multicast base port
     */
    public static void main(String[] args) {
        if(args.length < 1) {
            System.out.println("Expected parameters: <client port> [<multicast ip> <multicast port>]");
            return;
        }
        try
        {
            int clientPort = Integer.parseInt(args[0]);
            InetAddress multicastAddress = InetAddress.getByName((args.length < 2) ? "239.23.12.11" : args[1]);
            int basePort = (args.length < 3) ? 4049 : Integer.parseInt(args[2]);

            BasicClient client = new StatisticsClient(multicastAddress, basePort, clientPort);
            client.run();
        }
        catch(IOException ie) {
            System.out.println("Please specify a server ip address.");
        }
        catch(NumberFormatException nfe) {
            System.out.println("Please specify well-formed port numbers");
        }
    }

}
