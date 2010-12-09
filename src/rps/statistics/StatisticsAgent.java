package rps.statistics;

import agent.AbstractAgent;
import agent.discovery.Peer;
import java.net.InetAddress;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Gathers statistics from nearby servers.
 * @author Daniel Abrahamsson
 */
public class StatisticsAgent extends AbstractAgent {
    private final List<List<Tournament>> all;

    /**
     * Insantiates an RPS agent
     * @param clientAddress Home address
     * @param clientPort Home port
     */
    public StatisticsAgent(InetAddress clientAddress, int clientPort) {
        super(clientAddress, clientPort);
        all = new java.util.LinkedList<List<Tournament>>();
    }

    /**
     * Calculates and prints out all gathered statistics.
     */
    public void printStatistics() {
        if(!all.isEmpty()) {
            List<Tournament> tournaments = new java.util.LinkedList<Tournament>();
            Peer mostServer = null;
            int most = 0;
            Map<Peer, Integer> participationMap = new java.util.HashMap<Peer, Integer>();
            Map<Peer, Integer> winMap = new java.util.HashMap<Peer, Integer>();
            Tournament biggest = null;
            for(List<Tournament> local : all) {
                for(Tournament t : local) {
                    tournaments.add(t);
                    if(biggest == null)
                        biggest = t;
                    else {
                        if(t.getScoreboard().size() > biggest.getScoreboard().size())
                            biggest = t;
                    }

                    int position = 0;
                    int score = Integer.MAX_VALUE;
                    for(Tournament.ScoreboardEntry entry : t.getScoreboard()) {
                        if(entry.score < score) {
                            score = entry.score;
                            position++;
                        }
                        if(position == 1) {
                            Integer timesWon = winMap.get(entry.player);
                            if(timesWon == null)
                                winMap.put(entry.player, 1);
                            else
                                winMap.put(entry.player, timesWon+1);
                        }
                        Integer timesParticipated = participationMap.get(entry.player);
                        if(timesParticipated == null)
                            participationMap.put(entry.player, 1);
                        else
                            participationMap.put(entry.player, timesParticipated+1);
                    }
                }
                if(local.size() >= most) {
                    mostServer = local.get(0).getServer();
                    most = local.size();
                }
            }

            Entry<Peer, Integer> mostWins = max(winMap);
            Entry<Peer, Integer> mostParticipations = max(participationMap);

            System.out.println("A total of " + tournaments.size() +
                    " tournaments has been held at " + all.size() + " servers.");
            System.out.println(mostServer + " has hosted the most tournaments: " + most);
            System.out.println("The biggest tournament ever had " + biggest.getScoreboard().size() + 
                    " participants and was held at " + biggest.getServer());
            if(mostWins != null)
                System.out.println(mostWins.getKey() + " has won the most number of tournaments: " + mostWins.getValue());
            if(mostParticipations != null)
                System.out.println(mostParticipations.getKey() + " has taken part in the most number of tournaments: " + mostParticipations.getValue());
        }
        else
            System.out.println("I found no tournaments to report about.");
    }
   
    /**
     * Gathers statistics from nearby servers before returning.
     */
    @Override
    public void run() {
        if(currentServer != null) {
            if(currentServer instanceof StatisticsServer) {
                StatisticsServer ss = (StatisticsServer)currentServer;
                //Gather statistics
                all.add(ss.getTournaments());

                for(Peer server : currentServer.getNeighbours()) {
                    if(!visitedServers.contains(server))
                    {
                        migrate(server);
                        break;
                    }
                }
                if(currentServer != null)
                    migrate(getHome());
            }
        }
    }

    /**
     * A string representation of the agent.
     * @return A string representation of the agent.
     */
    @Override
    public String toString() {
        return "StatisticsAgent " + getHome();
    }

    /**
     * Extracts the entry with the biggest value.
     * @param map The map to search through.
     * @return The entry with the biggest value.
     */
    private Entry<Peer, Integer> max(Map<Peer, Integer> map) {
        int value = Integer.MIN_VALUE;
        Entry<Peer, Integer> max = null;
        for(Entry<Peer, Integer> entry : map.entrySet()) {
            if(entry.getValue() > value) {
                value = entry.getValue();
                max = entry;
            }
        }
        return max;
    }
}
