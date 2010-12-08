package agent.discovery;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Set;
;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.List;

/**
 * Discovers other peers.
 * @author Daniel Abrahamsson
 */
public class DiscoveryClient
{
    // Time used for discovery
    private static final int DISCOVERY_TIMEOUT = 3000;
    private Set<Peer> peers;
    private InetAddress multicastAddress;

    /**
     * Instantiates and runs a discovery problem.
     * @param mcastAddr The multicast address to use for discovery
     * @param basePort The port to send requests and receive responses on.
     * @throws IOException On communication error
     */
    public DiscoveryClient(InetAddress mcastAddr, int basePort)
          throws IOException
    {        
        peers = new java.util.HashSet<Peer>();
        multicastAddress = mcastAddr;
        MulticastSocket socket = new MulticastSocket(basePort);
        socket.setSoTimeout(DISCOVERY_TIMEOUT);
        socket.joinGroup(multicastAddress);
        discover(socket, basePort);
    }

    /**
     * Discovers other client's. Blocks for DISCOVERY_TIMEOUT ms.
     * @param socket The socket to send reqeusts and receive replies with.
     * @param basePort The port to send packets on.
     */
    private void discover(java.net.MulticastSocket socket, int basePort)
    {
        String data = "DISCOVER";
        DatagramPacket packet = new DatagramPacket(data.getBytes(), data.length(), multicastAddress, basePort);
        try {
            socket.send(packet);

            //Listen for reply
            while(true) {
                byte[] buf = new byte[255];
                DatagramPacket in = new DatagramPacket(buf, buf.length);
                socket.receive(in);
                data = new String(buf).trim();
                if(data.equals("REPLY")) {
                    peers.add(new Peer(in.getAddress(), in.getPort()));
                }
            }
        }
        catch(SocketTimeoutException ste) {
            //No more replies
        }
        catch(IOException ioe) { 
            //Communication error
        }
    }

    /**
     * @return A list of identified peers.
     * All peers in the list are guaranteed to be unique.
     */
    public List<Peer> getDiscoveryResult()
    {
        List<Peer> list = new java.util.ArrayList(peers.size());
        list.addAll(peers);
        return list;
    }

    /**
     * Test program. Runs a discovery client.
     * @param args args[0] ip, args[1] port
     */
    public static void main (String[] args)
    {
        try {
            String ip = (args.length >= 1) ? args[0] : "239.23.12.11";
            int basePort = (args.length >= 2) ? Integer.parseInt(args[1]) : 4049;

            DiscoveryClient client = new DiscoveryClient(InetAddress.getByName(ip), basePort);
            List<Peer> replies = client.getDiscoveryResult();
            System.out.println("Got " + replies.size() + " replies.");
            for(Peer peer : replies) {
                System.out.println(peer.ip + ":" + peer.port);
            }
        }
        catch(UnknownHostException e) {
            System.err.println("Unknown host!");
        }
        catch(NumberFormatException ne) {
            System.err.println("Invalid port number!");
        }
        catch(IOException ioe) {
            System.err.println("Communication error!");
        }
    }
}