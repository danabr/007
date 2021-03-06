package agent.server;

import agent.AgentServer;
import agent.discovery.DiscoveryClient;
import agent.discovery.Peer;
import java.io.IOException;
import java.net.InetAddress;
import java.util.List;

/**
 * Periodically runs a discovery for other
 * agents and puts them in a list.
 * @author Daniel Abrahamsson
 */
public class AgentDiscoveryThread extends Thread{
    AgentServer owner;
    private final InetAddress multicastAddress;
    private final int multicastPort;
    int interval;

    /**
     * Intitializes the discovery thread.
     * @param owner Owner of the thread. Will receive neighbour data.
     * @param multicastAddress The address to use for neighbour discovery.
     * @param multicastPort The port to use for neighbour discovery.
     * @param interval How often to run neighbour discovery.
     */
    public AgentDiscoveryThread(AgentServer owner, InetAddress multicastAddress, 
                                int multicastPort, int interval) {
        this.multicastAddress = multicastAddress;
        this.multicastPort = multicastPort;
        this.owner = owner;
        this.interval = interval;
    }

    /**
     * Runs neighbour discovery.
     */
    @Override
    public void run() {
        Peer serverID = owner.getId();
        while(true) {
            try {
                DiscoveryClient dc = new DiscoveryClient(multicastAddress, multicastPort);
                List<Peer> discoveryResult = dc.getDiscoveryResult();
                
                discoveryResult.remove(serverID); // Exclude local host
                owner.updateNeighbourList(discoveryResult);
                Thread.sleep(interval);
            }
            catch(InterruptedException ie) {}
            catch(IOException ioe) {
                //What to do here?
            }
        }
    }

}
