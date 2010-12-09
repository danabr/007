package rps.states;

import rps.Match;
import agent.Agent;
import agent.messaging.Message;
import rps.MessageTypes;
import rps.RPSAgent;
import agent.discovery.Peer;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Prepare a tournament.
 * @author Daniel Abrahamsson
 */
public class PrepareTournamentState extends agent.states.State<RPSAgent> {

    /**
     * Executes state logic.
     * @param agent The agent to perform state logic on.
     */
    public void execute(RPSAgent agent) {
        Set<Peer> slaves = agent.getSlaves();
        agent.log("It was time to prepare the tournament.");
        agent.log("First I had to find out how many of the " + slaves.size() + " participants was still willing to take part in the tournament.");

        //Find out how many slaves are still there
        Set<Peer> liveSlaves = new java.util.HashSet<Peer>();
        Map<Peer, RPSAgent> liveAgents = new java.util.HashMap<Peer, RPSAgent>();
        for(Agent neighbour : agent.getServer().getResidingAgents()) {
            if(slaves.contains(neighbour.getHome())) {
                if(liveSlaves.add(neighbour.getHome()))
                    liveAgents.put(neighbour.getHome(), (RPSAgent)neighbour);
            }
        }
        agent.setSlaves(liveSlaves);

        if(liveSlaves.size() > 1) {
            agent.log("It turned out to be " + liveSlaves.size() + ". Enough for a tournament to be held.");
            
            //Create match schema
            List<Match> matches = createMatchSchema(agent);

            //Send match schema to every slave
            agent.log("As soon I had created a match schema, I handed it out to all participants.");
            agent.log("There would be " + matches.size() + " matches.");
            for(Entry<Peer, RPSAgent> e : liveAgents.entrySet()) {
                e.getValue().postMessage(new Message(agent, MessageTypes.MATCHLIST, matches));
            }

            //Send start to each slave.
            agent.log("I can not describe the joy I felt when I finally could raise my voice and cry: 'Let the tournament begin!'");
            for(Entry<Peer, RPSAgent> e : liveAgents.entrySet()) {
                e.getValue().postMessage(new Message(agent, MessageTypes.START));
            }

            agent.setState(new RunTournamentState(matches));
        }
        else { //To few participants
            agent.log("Unfortunately, there were only " + liveSlaves.size() + " of them left.");
            agent.log("I had to cancel the tournament.");

            //Tell agents to go home
            Message goHome = new Message(agent, MessageTypes.GO_HOME, "There are not enough participants in the tournament. I'm sorry.");
            for(Entry<Peer, RPSAgent> e : liveAgents.entrySet()) {
                e.getValue().postMessage(goHome);
            }

            if(liveAgents.size() > 0)
                agent.log("It was a sad job to inform the remaining participants, but it had to be done.");

            agent.setState(new MoveHomeState());
        }
    }

    /**
     * Creates a match schema for the tournament.
     * @param agent The agent arranging the tournament.
     * @return A list of matches
     */
    private List<Match> createMatchSchema(RPSAgent agent) {
        Peer judge = agent.getHome();
        List<Peer> slaveList = new java.util.ArrayList<Peer>(agent.getSlaves());
        List<Match> matches = new java.util.LinkedList<Match>();
        for(int i  = 0; i < slaveList.size(); i++) {
            for(int j = i+1; j < slaveList.size(); j++) {
                matches.add(new Match(judge, slaveList.get(i), slaveList.get(j)));
            }
        }

        scrambleMatchList(matches);
        return matches;
    }

    /**
     * Scramble the order of the matches.
     * @param matches Collection of matches to be rearranged.
     */
    private void scrambleMatchList(List<Match> matches) {
        for(int i = 0; i < matches.size(); i++) {
            Match m = matches.remove((int)(Math.random()*(matches.size()-1)));
            matches.add(m);
        }
    }
}
