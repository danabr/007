package agent.discovery;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;

/**
 * Listens for other peers and replies to them.
 * @author Daniel Abrahamsson
 */
public class DiscoveryServer extends Thread
{
    private String ip;
    private int serverPort;
    private MulticastSocket receivingSocket;
    private MulticastSocket respondingSocket;
    private InetAddress multicastAddress;

    /**
     * Creates and starts a discovery server.
     * @param mcastAddr Multicast address to use
     * @param basePort Port to receive requests on.
     * @param serverPort Port to send replies from
     * @throws java.io.IOException On communication errors
     * @throws java.net.UnknownHostException If the own IP can not be resolved
     */
    public DiscoveryServer(InetAddress mcastAddr, int basePort, int serverPort)
          throws IOException, UnknownHostException
    {
        this.ip = InetAddress.getLocalHost().getHostAddress();
        this.serverPort = serverPort;
        this.multicastAddress = mcastAddr;
        this.receivingSocket = new MulticastSocket(basePort);
        this.respondingSocket = new MulticastSocket(serverPort);
        this.respondingSocket.setSoTimeout(1000);
        this.receivingSocket.joinGroup(multicastAddress);
        this.start();
    }

    /**
     * Runs the discovery server.
     */
    @Override
    public void run ()
    {
        String reply = "REPLY";
        while(true) {
            byte[] buf = new byte[255];
            DatagramPacket in = new DatagramPacket(buf, buf.length);
            try {
                receivingSocket.receive(in);
                String data = new String(buf).trim();
                if(data.equals("DISCOVER")) {
                    DatagramPacket out = new DatagramPacket(reply.getBytes(), reply.length(), multicastAddress, in.getPort());
                    respondingSocket.send(out);
                }
            }
            catch(IOException ioe) {
                System.out.println("Communication error!");
            }
        }
    }

    /**
     * Starts a discovery server.
     * @param args [0] = serverPort, [1] = ip, [2] = basePort
     */
    public static void main (String[] args)
    {
        if(args.length < 1) {
            System.out.println("Usage: DiscoveryServer <serverPort> <ip> <basePort>");
            return;
        }

        try {
            int serverPort = Integer.parseInt(args[0]);
            String ip = (args.length >= 2) ? args[1] : "239.23.12.11";
            int basePort = (args.length >= 3) ? Integer.parseInt(args[2]) : 4049;

            DiscoveryServer server = new DiscoveryServer(InetAddress.getByName(ip), basePort, serverPort);
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
