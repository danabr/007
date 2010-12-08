package rps.states;

import rps.RPSAgent;

/**
 * State for moving home with some message;
 * @author Daniel Abrahamsson
 */
public class MoveHomeState extends agent.states.State<RPSAgent> {

    /**
     * Executes state logic.
     * @param agent The agent to perform state logic on.
     */
    public void execute(RPSAgent agent) {
        agent.log("At last, I steered my legs home.");
        agent.log("After all my adventures, it would do me good with some rest.");
        agent.moveHome();
        agent.setState(null); // Die on server
    }
}
