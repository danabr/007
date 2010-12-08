package agent.server;

import agent.AbstractServer;
import agent.Agent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * A basic server serving any kind of agents
 * without adding additional support.
 */
public class BasicServer extends AbstractServer implements Runnable {
    private AgentDiscoveryThread discoveryThread;
    
    /**
     * Instantiates a server
     * @param multicastAddress Multicast address used for client and neighbour discovery
     * @param serverPort The port to listen to
     * @param basePort The multicast port used for client and neighbour discovery.
     * @throws IOException If a discovery server can not be set up.
     */
    public BasicServer(InetAddress multicastAddress, int basePort, int serverPort) throws IOException {
        super(serverPort);

        //Start discovery server
        new agent.discovery.DiscoveryServer(multicastAddress, basePort, serverPort);

        //Start agent discovery thread
        discoveryThread = new AgentDiscoveryThread(this, multicastAddress, basePort, 10000);
    }

    @Override
    public void run() {
        discoveryThread.start();
        try {
            ServerSocket server = new ServerSocket(serverPort);
            while(true) {
                Socket agentSocket = server.accept();

                try {
                    ObjectInputStream agentStream = new ObjectInputStream(agentSocket.getInputStream());
                    Agent agent = (Agent)agentStream.readObject();
                    residingAgents.add(agent);
                    agent.agentArrived(this, InetAddress.getLocalHost(), serverPort);
                } catch(ClassNotFoundException cfe) {
                    System.err.println("Unsupported object received: " + cfe.getMessage());
                }
                catch(IOException ioe) {
                    System.err.println("Tried to retrieve agent, but failed.");
                }
            }
        }
        catch(IOException ioe) {
            System.err.println("Failed to open server port");
        }
    }

    /**
     * Test program
     * @param args [0] serverPort [1] multicast IP [2] multicast port
     */
    public static void main(String[] args) {
        if(args.length < 1) {
            System.out.println("Args: <port> <multicast ip> <multicast port>");
            return;
        }

        try {
            int serverPort = Integer.parseInt(args[0]);
            InetAddress address = InetAddress.getByName((args.length < 2) ? "239.23.12.11" : args[1]);
            int basePort = (args.length < 3) ? 4049 : Integer.parseInt(args[2]);
            BasicServer server = new BasicServer(address, basePort, serverPort);
            server.run();
        }
        catch(NumberFormatException nfe) {
            System.err.println("Please provide a port number!");
        }
        catch(UnknownHostException he) {
            System.err.println("Unknown host!");
        }
        catch(IOException ioe) {
            System.err.println("Communication error");
        }
    }
}
