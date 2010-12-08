package rps.states;

import agent.messaging.Message;
import rps.MessageTypes;
import rps.RPSAgent;

/**
 * Only master agents may be in this state.
 * @author Daniel Abrahamsson
 */
public class FindWaitState extends agent.states.State<RPSAgent> {
    private final static int TIMEOUT = 3000;

    /**
     * Called when the owner enters the state.
     * This implementation does nothing.
     * @param owner
     */
    @Override
    public void enter(RPSAgent agent) {
        agent.log("Before moving on, I thought it would be good with some rest.");
        agent.log("I stayed at a local inn, looking for someone to chat with.");
    }

    /**
     * Executes state logic.
     * @param agent The agent to perform state logic on.
     */
    @Override
    public void execute(RPSAgent agent) {
        int timeToStay = TIMEOUT + (int)(Math.random()*1000);
        Message msg = agent.waitForMessage(MessageTypes.JOIN, timeToStay);
        if(msg.getType() != Message.TIMEOUT_MESSAGE) {
            RPSAgent sender = (RPSAgent)msg.getSender();
            agent.log("A stranger named " + sender.getHome() + " approached me, asking if I was willing to join his tournament.");
            if(sender.currentState() instanceof FindState) {
                if(sender.getSlaves().size() >= agent.getSlaves().size()) {
                    sender.postMessage(new Message(agent, MessageTypes.OK));
                    agent.setState(new PrepareJoinState(sender.getHome(), sender.getBattleServer()));
                    agent.log("'Sure', I said. His tournament was after all bigger than mine (" + sender.getSlaves().size() + " participants)");
                }
                else {
                    sender.postMessage(new Message(agent, MessageTypes.JOIN));
                    agent.addSlave(sender.getHome());
                    agent.log("'Why would I like to join your tiny tournament?' I asked.");
                    agent.log("His tournament had only " + sender.getSlaves().size() + " participants.");
                    agent.log("'No, join mine instead'. He left the table, mumbling that he would do so.");
                    agent.log("And so it happened I had now " + agent.getSlaves().size() + " participants in my tournament.");
                }
            }
        }
        else
        {
            agent.log("However, no one made any attempt to start a conversation.");
            agent.setState(new FindContinueState());
        }
    }
}
