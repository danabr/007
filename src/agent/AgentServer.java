package agent;

import agent.discovery.Peer;
import java.net.InetAddress;
import java.util.List;

/**
 * Interface defining the minimal set of
 * operations an agent server must support.
 */
public interface AgentServer
{
    /**
     *  The agent wants to be sent to inetAddr and port.
     *  @param agent reference to an agent object ("this")
     *  @param dstAddr destination address
     *  @param dstPort destination port
     */
    public void agentMigrate(Agent agent,
                             java.net.InetAddress dstAddr,
                             int dstPort);

    /**
     * @return A list of footprints
     */
    public List<Footprint> getFootprints();

    /**
     * @return Identifier (ip and port) of this server.
     */
    public Peer getId();

    /**
     * @return a list of neighbour servers.
     */
    public List<Peer> getNeighbours();

    /**
     * @return A list of agents currently residing on the server.
     */
    public List<Agent> getResidingAgents();

    /**
     * Updates the server's list of neighbours.
     * @param neighbours The new list of neighbours.
     */
    public void updateNeighbourList(List<Peer> neighbours);
}