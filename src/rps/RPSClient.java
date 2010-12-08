package rps;

import agent.Agent;
import agent.discovery.Peer;
import java.io.IOException;
import java.net.InetAddress;
import java.util.List;

/**
 * A client sending out agents to play Rock/Papers/Scissors.
 * @author Daniel Abrahamsson
 */
public class RPSClient extends agent.client.BasicClient {
    /**
     * Instantiates the client. The client will use
     * the opportunity to find a service point.
     * @param groupAddress Multicast address to use for service point discovery.
     * @param basePort Multicast port to use for service point discovery.
     * @param clientPort The port used to receive agents.
     * @throws IOException If peer discovery failed.
     */
    public RPSClient(InetAddress groupAddress, int basePort, int clientPort) throws IOException {
        super(groupAddress, basePort, clientPort);
    }

    /**
     * Returns a list of agents to be sent to the server.
     * @return a list of agents to be sent to the server.
     */
    @Override
    protected List<Agent> initAgents() {
        List<Agent> agents = new java.util.LinkedList<Agent>();
        agents.add(new RPSAgent(getClientAddress(), clientPort));
        return agents;
    }

    /**
     * Called when the client receives an agent from a server.
     * @param agent The newly retrieved agent
     */
    @Override
    protected void onAgentReceived(Agent agent) {
        RPSAgent rps = (RPSAgent)agent;
        System.out.println("Client is back! Visited:");
        for(Peer p : rps.getVisitedServers()) {
            System.out.println(p);
        }
        System.out.println("History: ");
        rps.speakThyTounge();
    }

    /**
     * Called if an agent could not be delivered to the service point.
     * @param agent The agent that we failed to deliver.
     * @param reason The reason of the error.
     */
    @Override
    protected void onDeliveryError(Agent agent, Exception reason) {
        System.err.println("Failed to deliver agent " + agent);
        System.err.println("Reason: " + reason);
    }

    /**
     * Called when we failed to receive an agent.
     * @param reason The reason of the error
     */
    @Override
    protected void onReceiveError(Exception reason) {
        throw new UnsupportedOperationException("Not supported yet.");
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

            RPSClient client = new RPSClient(multicastAddress, basePort, clientPort);
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
