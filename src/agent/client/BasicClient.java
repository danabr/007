package agent.client;

import agent.Agent;
import agent.discovery.DiscoveryClient;
import agent.discovery.Peer;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

/**
 * Contains common client funtionality.
 * @author Daniel Abrahamsson
 */
public abstract class BasicClient implements Runnable {
    private int agentsDelivered = 0;
    private final InetAddress serverAddress;
    private final int serverPort;
    /** The port used for receiving agents */
    protected final int clientPort;

    /**
     * Instantiates the client. The client will use
     * the opportunity to find a service point.
     * @param groupAddress Multicast address to use for service point discovery.
     * @param basePort Multicast port to use for service point discovery.
     * @param clientPort The port used to receive agents.
     * @throws IOException If service point discovery failed.
     * @throws IllegalStateException If no service point could be found.
     */
    public BasicClient(InetAddress groupAddress, int basePort, int clientPort) throws IOException {
        this.clientPort = clientPort;

        //Discover servers
        DiscoveryClient dc = new DiscoveryClient(groupAddress, basePort);
        java.util.List<Peer> servers = dc.getDiscoveryResult();
        if(servers.isEmpty()) {
            throw new IllegalStateException("Did not find any server to connect to.");
        }

        //Select one of the servers as service point
        Peer servicePoint = servers.get((int)(Math.random() * (servers.size()) - 1));
        this.serverAddress = servicePoint.ip;
        this.serverPort = servicePoint.port;
    }

    /**
     * Runs the client.
     * Sends all agents to the service point
     * and waits for them to return.
     */
    public void run() {
        List<Agent> agents = initAgents();

        //Send all agents
        for(Agent agent : agents)
            deliverAgent(agent);

        //Wait for all agents to return
        for(int i = 0; i < agentsDelivered; i++)
            receiveAgent();
    }

    /**
     * Delivers an agent to the service point server.
     * @param agent The agent to be sent.
     */
    protected void deliverAgent(Agent agent) {
        ObjectOutputStream agentStream = null;
        try {
            Socket serverSocket = new Socket(serverAddress, serverPort);
            agentStream = new ObjectOutputStream(serverSocket.getOutputStream());
            agentStream.writeObject(agent);
            agentsDelivered++;
        }
        catch(IOException ioe) {
            onDeliveryError(agent, ioe);
        }
        finally {
            try { if(agentStream != null) agentStream.close(); } catch(IOException ioe) {}
        }
    }

    /**
     * Returns a list of agents to be sent to the server.
     * @return a list of agents to be sent to the server.
     */
    protected abstract List<Agent> initAgents();

    /**
     * Returns the clients ip address.
     * This may be null if the client does not have a network card.
     * @return The clients ip address
     */
    protected InetAddress getClientAddress() {
        try {
            return InetAddress.getLocalHost();
        }
        catch(IOException ioe) {
            System.err.println("Localhost could not be identified!");
        }
        return null;
    }

    /**
     * Called when the client receives an agent from a server.
     * @param agent The newly retrieved agent
     */
    protected abstract void onAgentReceived(Agent agent);

    /**
     * Called if an agent could not be delivered to the service point.
     * @param agent The agent that we failed to deliver.
     * @param reason The reason of the error.
     */
    protected abstract void onDeliveryError(Agent agent, Exception reason);

    /**
     * Called when we failed to receive an agent.
     * @param reason The reason of the error
     */
    protected abstract void onReceiveError(Exception reason);

    /**
     * Waits to receive a client.
     */
    private void receiveAgent() {
        ObjectInputStream agentStream = null;
        try {
            ServerSocket listeningSocket = new ServerSocket(clientPort);
            Socket replySocket = listeningSocket.accept();
            agentStream = new ObjectInputStream(replySocket.getInputStream());
            Agent agent = (Agent)agentStream.readObject();
            onAgentReceived(agent);
        }
        catch(Exception ioe) {
            onReceiveError(ioe);
        }
        finally {
            try { if(agentStream != null) agentStream.close(); } catch(IOException ioe) {}
        }
    }
}
