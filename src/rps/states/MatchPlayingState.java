package rps.states;

import agent.messaging.Message;
import rps.Match;
import rps.MessageTypes;
import rps.RPSAgent;
import agent.discovery.Peer;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * State for playing through a series of matches.
 * @author Daniel Abrahamsson
 */
public class MatchPlayingState extends agent.states.State<RPSAgent> {
    private final List<Match> matches;
    private int matchIndex;

    /**
     * Instantiates the state.
     * @param matches Matches to be played.
     */
    public MatchPlayingState(List<Match> matches) {
        this.matches = matches;
        this.matchIndex = 0;
    }

    /**
     * Executes state logic.
     * @param agent The agent to perform state logic on.
     */
    public void execute(RPSAgent agent) {
        Peer id = agent.getHome();
        agent.log("Eagerly I looked through the tournament scheme for my next match.");
        while(matchIndex < matches.size()) {
            Match match = matches.get(matchIndex++);
            if(match.isTakingPart(id)) {
                agent.log("My smile reached my ears when I saw the following entry: " + match);
                agent.setState(new MatchState(this, match));
                break;
            }
        }
        
        if(agent.currentState() == this)  { //All matches have been played.
            agent.log("But there were no more matches for my part.");
            agent.log("The tournament was over. Now I just waited for the statistics.");

            Message msg = agent.waitForMessage(agent.getMaster(), 30000);
            if(msg.getType() == MessageTypes.STATISTICS) {
                agent.log("This was the final scoreboard: ");
                Map<Peer, Integer> scoreTable = (Map<Peer, Integer>)msg.getData();
                for(Entry<Peer, Integer> entry : scoreTable.entrySet()) {
                    agent.log(entry.getKey() + ": "  + entry.getValue() + " points.");
                }
            }
            else
                agent.log("But the tournament master did not hand them out.");
            agent.setState(new MoveHomeState());
        }
    }

}
