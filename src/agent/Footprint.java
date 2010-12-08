package agent;

import agent.discovery.Peer;

/**
 * Represents a footprint left by an agent on a server.
 * @author Daniel Abrahamsson
 */
public class Footprint {
    private long timestamp;
    private Peer home;
    private Peer destination;

    /**
     * Instantiates a new footprint
     * @param home The home of the agent who left the footprint
     * @param destination The destination the agent left to
     */
    public Footprint(Peer home, Peer destination) {
        this.home = home;
        this.destination = destination;
        timestamp = System.currentTimeMillis();
    }
    
    /**
     * @return The home of the agent who left the footprint.
     */
    public Peer getHome() { 
        return home;
    }
    
    /**
     * @return The peer to which the agent traveled.
     */
    public Peer getDestination() {
        return destination;
    }

    /**
     * @return The time when the footprint was created.
     */
    public long getTimestamp() {
        return timestamp;
    }

    /**
     * @return A string representation of the footprint
     */
    @Override
    public String toString() {
        return home.toString() + " => " + destination.toString();
    }
}
