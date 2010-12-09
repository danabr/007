package rps.states;

import agent.messaging.Message;
import rps.MessageTypes;
import rps.RPSAgent;

/**
 * Wait for last minute participants.
 * This state is only possible for master agents.
 * @author Daniel Abrahamsson
 */
public class WaitForLatecomersState extends agent.states.State<RPSAgent> {
    // Time (in ms) to wait for latecomers
    private static final int WAIT_TIME = 5000;

    /**
     * Executes state logic.
     * @param agent The agent to perform state logic on.
     */
    @Override
    public void execute(RPSAgent agent) {
        long end = System.currentTimeMillis() + WAIT_TIME;
        while(System.currentTimeMillis() < end) {
            Message msg = agent.waitForMessage(MessageTypes.JOIN, 200);
            if(msg.getType() != Message.TIMEOUT_MESSAGE) {
                Message response = new Message(agent, MessageTypes.OK);
                ((RPSAgent)msg.getSender()).postMessage(response);
                agent.addSlave(msg.getSender().getHome());
            }
        }

        agent.setState(new PrepareTournamentState());
    }
}
