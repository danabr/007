package agent;

import agent.discovery.Peer;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.List;

/**
 * Contains common agent server behaviour.
 * @author Daniel Abrahamsson
 */
public abstract class AbstractServer implements AgentServer {
    /** Server port for receiving agents */
    protected final int serverPort;
    /** A list of neighbouring servers */
    protected final List<Peer> neighbours;
    /** A list of footprints left by agents */
    protected final List<Footprint> footprints;
    /** A list of agents residing on the server */
    protected final List<Agent> residingAgents;

    /**
     * Initializes the server..
     * @param serverPort Port to receive agents on.
     */
    public AbstractServer(int serverPort) {
        this.serverPort = serverPort;
        this.neighbours = new java.util.LinkedList<Peer>();
        this.footprints = new java.util.LinkedList<Footprint>();
        this.residingAgents = Collections.synchronizedList(new java.util.LinkedList<Agent>());
    }

    /**
     *  The agent wants to be sent to inetAddr and port.
     *  @param agent reference to an agent object ("this")
     *  @param dstAddr destination address
     *  @param dstPort destination port
     */
    public void agentMigrate(Agent agent, InetAddress dstAddr, int dstPort) {
        try {
            System.out.println("Migrating " + agent + " to " + dstAddr.getHostAddress() + ":" + dstPort);
            Socket migrationSocket = new Socket(dstAddr, dstPort);
            ObjectOutputStream agentStream = new ObjectOutputStream(migrationSocket.getOutputStream());
            agentStream.writeObject(agent);
            residingAgents.remove(agent);
            footprints.add(0, new Footprint(agent.getHome(), new Peer(dstAddr, dstPort)));
            agentStream.close();
        }
        catch(IOException ioe) {
            System.out.println("Failed to migrate client!");
        }
    }

    /**
     * Returns a list of footprints. The footprints
     * are guaranteed to be sorted in such a way that
     * the most recent footprint is placed first.
     * @return A list of footprints
     */
    public List<Footprint> getFootprints() {
        return new java.util.ArrayList<Footprint>(footprints);
    }

    /**
     * @return A Peer object identifying this server.
     */
    public Peer getId() {
        try {
            return new Peer(InetAddress.getLocalHost(), serverPort);
        } catch(UnknownHostException uhe) {} //Should never happen
        throw new IllegalStateException("How did I end up here?");
    }

    /**
     * @return a list of neighbour servers.
     */
    public List<Peer> getNeighbours() {
        return new java.util.ArrayList<Peer>(neighbours);
    }
    
    /**
     * @return A list of agents currently residing on the server.
     */
    public List<Agent> getResidingAgents() {
        return new java.util.ArrayList<Agent>(residingAgents);
    }

    /**
     * Updates the server's list of neighbours.
     * @param neighbours The new list of neighbours.
     */
    public synchronized void updateNeighbourList(List<Peer> neighbours) {
        this.neighbours.clear();
        this.neighbours.addAll(neighbours);
    }
}
