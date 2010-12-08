package rps.states;

import agent.Agent;
import agent.messaging.Message;
import rps.Match;
import rps.MessageTypes;
import rps.RPSAgent;
import agent.discovery.Peer;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * The host keeps record of all match results,
 * and also acts as a judge.
 * @author Daniel Abrahamsson
 */
public class RunTournamentState extends agent.states.State<RPSAgent> {
    private List<Match> matches;

    /**
     * Initializes the state.
     * @param matches Matches to be played
     */
    public RunTournamentState(List<Match> matches) {
        this.matches = matches;
    }

    /**
     * Executes state logic.
     * @param agent The agent to perform state logic on.
     */
    @Override
    public void execute(RPSAgent agent) {
        List<Agent> agents = agent.getServer().getResidingAgents();
        agent.log("As the tournament master, I would also judge all games.");
        //Act as judge for each match
        int matchNo = 0;
        for(Match match : matches) {
            matchNo++;
            RPSAgent p1 = findAgent(agents, match.participant1);
            RPSAgent p2 = findAgent(agents, match.participant2);
            Message ready = new Message(agent, MessageTypes.READY);
            Message go = new Message(agent, MessageTypes.GO);

            agent.log("The next match was match number " + matchNo + ": " + match);

            //Send READY to participants
            agent.log("The audience fell quiet when they heard my voice: 'Are you ready?'");
            p1.postMessage(ready); p2.postMessage(ready);
            //Wait for READY response
            Message r1 = agent.waitForMessage(MessageTypes.READY, 5000);
            Message r2 = agent.waitForMessage(MessageTypes.READY, 5000);

            if(r1.getType() == MessageTypes.READY && r2.getType() == MessageTypes.READY) {
                agent.log("Both participants answered loudly.");
                int roundsLeft = 9; //So we do not run forever.
                while(!match.isDone() && roundsLeft > 0) {
                    p1.postMessage(go);
                    r1 = agent.waitForMessage(MessageTypes.PLAY, 2000);
                    p2.postMessage(go);
                    r2 = agent.waitForMessage(MessageTypes.PLAY, 2000);
                    playMove(agent, match, r1, r2);
                    roundsLeft--;
                }
                agent.log("And so the match was over.");
                if(match.getWinner() != null)
                    agent.log("'And the winner of the game is " + match.getWinner() + "'");
                else
                    agent.log("After 9 rounds of play, the match was cancelled.");

                Message results = new Message(agent, MessageTypes.MATCH_RESULTS, match);
                p1.postMessage(results); p2.postMessage(results);
            }
            else {
                agent.log("Unfortunately, one of the participants did not answer. We had to skip the match.");
            }
        }

        agent.log("Eventually, all games had been played.");
        //Calculate statistics
        Map<Peer, Integer> scoreTable = new java.util.HashMap<Peer, Integer>();

        for(Match match : matches) {
            if(scoreTable.get(match.participant1) == null)
                scoreTable.put(match.participant1, 0);
            if(scoreTable.get(match.participant2) == null)
                scoreTable.put(match.participant2, 0);

            Peer winner = match.getWinner();
            if(winner != null) {
                Integer oldScore = scoreTable.get(winner);
                scoreTable.put(winner, oldScore+1);
            }
        }

        agent.log("The results could be summarized as follows: ");
        for(Entry<Peer, Integer> entry : scoreTable.entrySet()) {
            agent.log(entry.getKey() + ": "  + entry.getValue() + " points.");
        }

        //Send statistics to each participant
        agent.log("I handed out the scores to the impatient participants.");
        Message msg = new Message(agent, MessageTypes.STATISTICS, scoreTable);
        Set<Peer> slaves = agent.getSlaves();
        for(Agent neighbour : agents) {
            if(slaves.contains(neighbour.getHome())) {
                ((RPSAgent)neighbour).postMessage(msg);
            }
        }

        agent.log("And thus, the tournament was over.");
        //At last, go home.
        agent.setState(new MoveHomeState());
    }

    /**
     * Finds an agent based on peer.
     * @param agents Agent collection to search for the agent.
     * @param p Used to identify agent.
     * @return The found agent, or null if agent could not be found.
     */
    private RPSAgent findAgent(List<Agent> agents, Peer p) {
        for(Agent a : agents) {
            if(a.getHome().equals(p) && a instanceof RPSAgent)
                return (RPSAgent)a;
        }
        return null;
    }

    /**
     * Inteprets participant messages as moves.
     * @param agent The current agent
     * @param match The current match
     * @param move1 Message containing first player move.
     * @param move2 Message containg second player move.
     */
    private void playMove(RPSAgent agent, Match match, Message move1, Message move2) {
        Integer p1Move = (move1.getType() == MessageTypes.PLAY) ? (Integer)move1.getData() : Match.NO_MOVE;
        Integer p2Move = (move2.getType() == MessageTypes.PLAY) ? (Integer)move2.getData() : Match.NO_MOVE;
        agent.log(match.participant1 + " played " + Match.moveToString(p1Move) +
                " against " + match.participant2 + "'s " + Match.moveToString(p2Move) + ".");       
        match.play(p1Move, p2Move);
        agent.log(match.score() + " was scribbled down on the scoreboard.");
    }
}
