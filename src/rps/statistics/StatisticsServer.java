package rps.statistics;

import agent.discovery.Peer;
import agent.server.BasicServer;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.util.List;
import rps.util.Tuple;

/**
 * Basic server with the additional capability of storing
 * tournament statistics.
 * @author Daniel Abrahamsson
 */
public class StatisticsServer extends BasicServer {
    private final List<Tournament> tournaments;

    /**
     * Instantiates a server
     * @param multicastAddress Multicast address used for client and neighbour discovery
     * @param serverPort The port to listen to
     * @param basePort The multicast port used for client and neighbour discovery.
     * @throws IOException If a discovery server can not be set up.
     */
    public StatisticsServer(InetAddress multicastAddress, int basePort, int serverPort) throws IOException {
        super(multicastAddress, basePort, serverPort);
        tournaments = new java.util.LinkedList<Tournament>();
        loadTournamentData();
    }

    /**
     * Returns data about all tournaments held at the server.
     * @return A list with all tournaments held at the server.
     */
    public synchronized List<Tournament> getTournaments() {
        return tournaments;
    }

    /**
     * Stores tournament statistics
     * @param scoreboard Tournament scoreboard
     */
    public void storeTournament(List<Tuple<Peer, Integer>> scoreboard) {
        tournaments.add(new Tournament(this.getId(), scoreboard));
        storeTournamentData();
    }

    /**
     * A string representation of the server.
     * @return A string representation of the server.
     */
    @Override
    public String toString() {
        return "RPSServer " + getId();
    }

    /**
     * Loads tournament data from file.
     */
    private synchronized void loadTournamentData() {
        File path = new File("server/" + toString().replace(':', '.') + ".dat");
        if(path.exists()) {
            ObjectInputStream oi = null;
            try
            {
                oi = new ObjectInputStream(new FileInputStream(path));
                List<Tournament> data = (List<Tournament>)oi.readObject();
                tournaments.addAll(data);
            }
            catch(IOException ioe) {
                System.err.println("Failed to load data file: " + ioe);
            }
            catch(ClassNotFoundException cnf) {
                System.out.println("Corrupted data file. Got: " + cnf);
            }
            finally {
                try { if(oi != null) oi.close(); } catch(IOException ioe) {}
            }
        }
        else
            System.out.println("No data file found.");
    }

    /**
     * Stores tournament data in data file.
     */
    private synchronized void storeTournamentData() {
        File path = new File("server/" + toString().replace(':', '.') + ".dat");
        ObjectOutputStream oo = null;
        try
        {
            oo = new ObjectOutputStream(new FileOutputStream(path, false));
            oo.writeObject(tournaments);
            oo.flush();
        }
        catch(IOException ioe) {
            System.err.println("Failed to store tournament data: " + ioe);
        }
        finally {
            try { if(oo != null) oo.close(); } catch(IOException ioe) {}
        }
    }
}
