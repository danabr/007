package rps;

import agent.server.BasicServer;
import java.io.IOException;
import java.net.InetAddress;

/**
 * A program for running several servers at once.
 * @author Daniel Abrahamsson
 */
public class ServerRunner {
    /**
     * Starts several servers at once.
     * @param args [0] - Number of servers to start
     * @throws IOException if the multicast group is not recognized.
     */
    public static void main(String[] args) {
        if(args.length >= 1) {
            try {
                int servers = Integer.parseInt(args[0]);
                String mIP = (args.length >= 2) ? args[1] : "239.23.12.11";
                InetAddress address = InetAddress.getByName(mIP);
                int basePort = (args.length >= 3) ? Integer.parseInt(args[2]) : 4049;
                int serverPort = 10000;
                for(int i = 0; i < servers; i++) {
                    try {
                        BasicServer server = new BasicServer(address, basePort, serverPort + i);
                        Thread serverRunner = new Thread(server);
                        serverRunner.start();
                        System.out.println("Started server " + server.getId());
                    }
                    catch(IOException ioe) {
                        System.err.println("Failed to start server " + (serverPort+i) + ". " + ioe.getMessage());
                    }
                }
            }
            catch(IOException ioe) {
                System.err.println("Invalid multicast IP");
            }
            catch(NumberFormatException nfe) {
                System.err.println("Invalid port number");
            }
        }
        else {
            System.out.println("Usage: java -cp build\\classes agent.rps.ServerRunner <numservers> [<multicast ip> <multicast port>]");
        }
    }
}
