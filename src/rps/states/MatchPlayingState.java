package rps.states;

import agent.messaging.Message;
import rps.Match;
import rps.MessageTypes;
import rps.RPSAgent;
import agent.discovery.Peer;
import java.util.List;
import rps.util.Tuple;

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
    @Override
    public void execute(RPSAgent agent) {
        agent.log("Eagerly I looked through the tournament scheme for my next match.");
        playNextMatch(agent);
        
        if(agent.currentState() == this)  { //All matches have been played.
            agent.log("But there were no more matches for my part.");
            agent.log("The tournament was over. Now I just waited for the statistics.");
            waitForStatistics(agent);
            agent.setState(new MoveHomeState());
        }
    }

    /**
     * Plays the next match, if there is any.
     * @param RPSAgent agent The match playing agent
     */
    private void playNextMatch(RPSAgent agent) {
        Peer id = agent.getHome();
        while(matchIndex < matches.size()) {
            Match match = matches.get(matchIndex++);
            if(match.isTakingPart(id)) {
                agent.log("My smile reached my ears when I saw the following entry: " + match);
                agent.setState(new MatchState(this, match));
                break;
            }
        }
    }

    /**
     * Waits for statistics from the tournament master
     * and prints them.
     * @param agent The agent
     */
    private void waitForStatistics(RPSAgent agent) {
        Message msg = agent.waitForMessage(agent.getMaster(), 30000);
        if(msg.getType() == MessageTypes.STATISTICS) {
            agent.log("This was the final scoreboard: ");
            List<Tuple<Peer, Integer>> scoreTable = (List<Tuple<Peer, Integer>>)msg.getData();
            int position = 0;
            int lastScore = Integer.MAX_VALUE;
            int myPosition = 0;
            for(Tuple<Peer, Integer> entry : scoreTable) {
                if(entry.b < lastScore)
                    position++;
                if(entry.a.equals(agent.getHome()))
                    myPosition = position;
                agent.log(position + ". " + entry.a + ": "  + entry.b + " points.");
            }

            if(myPosition == 1)
                agent.log("I had won the tournament! What a splendid day it had been!");
            else if(myPosition < position / 2)
                agent.log("I was quite content with my result.");
            else
                agent.log("I was truly dissapointed with my performance.");
        }
        else
            agent.log("But the tournament master did not hand them out.");
    }

}
