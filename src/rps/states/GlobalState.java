package rps.states;

import rps.RPSAgent;

/**
 * Global RPS agent state. Makes sure the
 * agent eventually returns home.
 * @author Daniel Abrahamsson
 */
public class GlobalState extends agent.states.State<RPSAgent> {
    private final long returnTime;


    /**
     *
     * @param returnTime
     */
    public GlobalState(long returnTime) {
        this.returnTime = returnTime;
    }
    
    
    /**
     * Executes state logic.
     * @param agent
     */
    @Override
    public void execute(RPSAgent agent) {
        if(System.currentTimeMillis() >= returnTime) {
            agent.log("Suddenly I remembered I had promised Mama to be home before dusk.");
            agent.setState(new MoveHomeState());
        }
    }
}
