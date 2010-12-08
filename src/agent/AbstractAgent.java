package agent;

import agent.discovery.Peer;
import java.io.Serializable;
import java.net.InetAddress;
import java.util.List;

/**
 * Encapsulates common agent behaviour.
 * @author Daniel Abrahamsson
 */
public abstract class AbstractAgent extends Thread implements Agent, Serializable {
    /** The home address */
    protected final InetAddress clientAddress;
    /** The home port */
    protected final int clientPort;
    /** The server the agent is residing on. */
    protected AgentServer currentServer;
    /** A list of servers visited by the agent. */
    protected final List<Peer> visitedServers;

    /**
     * Insantiates an abstract agent
     * @param clientAddress Home address
     * @param clientPort Home port
     */
    public AbstractAgent(InetAddress clientAddress, int clientPort) {
        this.clientAddress = clientAddress;
        this.clientPort = clientPort;
        this.visitedServers = new java.util.LinkedList<Peer>();
    }

    /**
      * An agent server invokes this method. It is used to tell an agent
      * that he is successfully arrived at a server and is ready to run.
      * @param srv reference to that server object that received the agent object
      * @param srvInetAddr the server's IP address
      * @param serverPort  the server's server port
     */
    public void agentArrived(AgentServer srv, InetAddress srvInetAddr, int serverPort) {
        this.currentServer = srv;
        visitedServers.add(new Peer(srvInetAddr, serverPort));
        start();
    }

    /**
     * Returns the agent's home peer.
     * @return The agent's home (where it was originally sent from)
     */
    public Peer getHome() {
        return new Peer(clientAddress, clientPort);
    }

    /**
     * Returns a list of all visited servers.
     * @return A list of all visited servers
     */
    public List<Peer> getVisitedServers() {
        return visitedServers;
    }

    /**
     * Migrates the agent to another server.
     * @param destination Identifies the server to migrate to.
     */
    public synchronized final void migrate(Peer destination) {
        onMigrate();
        if(!destination.equals(currentServer.getId())) { //Only move if we have to
            AgentServer server = this.currentServer;
            this.currentServer = null; //Not Serializable
            server.agentMigrate(this, destination.ip, destination.port);
        }
    }

    /**
     * Requests migration to the agent's home.
     */
    public void moveHome() {
        migrate(getHome());
    }

    /**
     * Called when the agent is about to migrate.
     */
    public void onMigrate() {}

    /**
     * Runs the agent.
     * An abstract agent does nothing.
     */
    @Override
    public void run() { }
}
