package rps.states;

import agent.messaging.Message;
import rps.Match;
import rps.MessageTypes;
import rps.RPSAgent;

/**
 * Play a game.
 * @author Daniel Abrahamsson
 */
public class MatchState extends agent.states.State<RPSAgent> {
    private final agent.states.State<RPSAgent> returnState;
    private final Match match;

    /**
     * Initializes math state.
     * @param returnState State to return to when match has finished
     * @param match Match to play
     */
    public MatchState(agent.states.State<RPSAgent> returnState, Match match) {
        this.returnState = returnState;
        this.match = match;
    }

    /**
     * Executes state logic.
     * @param agent The agent to perform state logic on.
     */
    public void execute(RPSAgent agent) {
        agent.log("While waiting for the judge to call my name, I practised my moves.");
        Message msg = agent.waitForMessage(match.judge, 30000);
        if(msg.getType() == MessageTypes.READY) {
            agent.log("'" + agent + "!' the judge cried. 'Are you ready?'");
            RPSAgent judge = (RPSAgent)msg.getSender();
            judge.postMessage(new Message(agent, MessageTypes.READY));
            agent.log("I hurried to answer him. My heart beat like a drum in my chest.");
            int rounds = 0;
            while(true) {
                msg = agent.waitForMessage(match.judge, 5000);
                if(msg.getType() == MessageTypes.GO) {
                    rounds++;
                    int move = 1 + (int)(Math.random()*3);
                    judge.postMessage(new Message(agent, MessageTypes.PLAY, move));
                }
                else if(msg.getType() == MessageTypes.MATCH_RESULTS) {
                    Match results = (Match)msg.getData();
                    agent.log("After " + rounds + " rounds, the match was over.");
                    if(results.getWinner() == null)
                        agent.log("A tie! I could not believe it!");
                    else {
                        agent.log("'And the winner is... " + results.getWinner() + "!'");
                        if(results.getWinner().equals(agent.getHome()))
                            agent.log("I won! Ha! I knew I was the better one.");
                        else
                            agent.log("How could I lose? I had been practising for months!");
                    }
                    break;
                }
                else { //Timeout
                    agent.log("I was surprised when the judge suddenly went quiet.");
                    agent.log("No one could answer what had happened. We had to cancel the game.");
                    break;
                }
            }
        }
        else {
            agent.log("But the judge never called my name! What a dissapointment!");
            agent.log("'Don't let it push you down', I said to myself. 'Prepare for the next match instead.'");
        }
        
        agent.setState(returnState);
    }

}
