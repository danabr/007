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
import rps.util.Tuple;

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
        agent.log("The results could be summarized as follows: ");
        judgeMatches(agent, agents);
        agent.log("Eventually, all games had been played.");
        List<Tuple<Peer, Integer>> sortedScoreTable = makeScoreBoard();
        sendStatistics(agent, agents, sortedScoreTable);
        logSummary(agent, sortedScoreTable);
        agent.log("And thus, the tournament was over.");
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

    /**
     * Judges the given match.
     * @param agent The judge
     * @param match The match to be judged.
     * @param agents Used to identify agents.
     */
    private void judgeMatch(RPSAgent agent, Match match, List<Agent> agents) {
        RPSAgent p1 = findAgent(agents, match.participant1);
        RPSAgent p2 = findAgent(agents, match.participant2);
        Message ready = new Message(agent, MessageTypes.READY);
        Message go = new Message(agent, MessageTypes.GO);

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

    /**
     * Goes through all matches and judges them.
     * @param agent The judge
     * @param agents Agents on the server.
     */
    private void judgeMatches(RPSAgent agent, List<Agent> agents) {
        agent.log("As the tournament master, I would also judge all games.");
        int matchNo = 0;
        for(Match match : matches) {
            matchNo++;
            agent.log("The next match was match number " + matchNo + ": " + match);
            judgeMatch(agent, match, agents);
        }
    }

    /**
     * Creates and returns the tournaments score board.
     * @return A scoreboard
     */
    private List<Tuple<Peer, Integer>> makeScoreBoard() {
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

        List<Tuple<Peer, Integer>> sortedScoreTable = new java.util.LinkedList<Tuple<Peer, Integer>>();
        for(Entry<Peer, Integer> entry : scoreTable.entrySet()) {
            int i = 0;
            for(; i < sortedScoreTable.size() && sortedScoreTable.get(i).b > entry.getValue(); i++);
            if(i < sortedScoreTable.size())
                sortedScoreTable.add(i, new Tuple<Peer, Integer>(entry.getKey(), entry.getValue()));
            else
                sortedScoreTable.add(new Tuple<Peer, Integer>(entry.getKey(), entry.getValue()));
        }

        return sortedScoreTable;
    }

    /**
     * Logs the score table to in the agent's journal.
     * @param agent The tournament master
     * @param sortedScoreTable The score table
     */
    private void logSummary(RPSAgent agent, List<Tuple<Peer, Integer>> sortedScoreTable) {
        int position = 0;
        int lastScore = Integer.MAX_VALUE;
        for(Tuple<Peer, Integer> entry : sortedScoreTable) {
            if(entry.b < lastScore)
                position++;
            lastScore = entry.b;
            agent.log(position + ". " + entry.a + ": "  + entry.b + " points.");
        }
    }

    /**
     * Distributes the score table to the tournament participants.
     * @param agent The tournament master
     * @param agents Neighbouring agents
     * @param sortedScoreTable The scoretable
     */
    private void sendStatistics(RPSAgent agent, List<Agent> agents, List<Tuple<Peer, Integer>> sortedScoreTable) {
        //Send statistics to each participant
        agent.log("I handed out the scores to the impatient participants.");
        Message msg = new Message(agent, MessageTypes.STATISTICS, sortedScoreTable);
        Set<Peer> slaves = agent.getSlaves();
        for(Agent neighbour : agents) {
            if(slaves.contains(neighbour.getHome())) {
                ((RPSAgent)neighbour).postMessage(msg);
            }
        }
    }
}
