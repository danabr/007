package agent.discovery;

import java.io.Serializable;
import java.net.InetAddress;

/**
 * Encapsulates data about identified peers.
 * @author Daniel Abrahamsson
 */
public class Peer implements Serializable {
    public InetAddress ip;
    public int port;

    public Peer(InetAddress ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    /**
     * Returns true if both objects are equal.
     * @param o The object to compare with.
     * @return true if the objects are equal
     */
    @Override
    public boolean equals(Object o) {
        if(o instanceof Peer) {
            Peer p2 = (Peer)o;
            return this.port == p2.port && p2.ip.equals(this.ip);
        }
        return false;
    }

    /**
     * @return A hashcode
     */
    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    /**
     * @return A string representation of the peer.
     */
    @Override
    public String toString() {
        return ip.getHostAddress() + ":" + port;
    }
}

