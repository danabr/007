package rps.states;

import agent.messaging.Message;
import agent.Agent;
import rps.MessageTypes;
import rps.RPSAgent;
import java.util.List;

/**
 * Find other participants willing to join our tournament.
 * Only master agents may be in this state.
 * @author Daniel Abrahamsson
 */
public class FindState extends agent.states.State<RPSAgent> {
    private static final int TIMEOUT = 1500;

    /**
     * Executes state logic.
     * @param agent The agent to perform state logic on.
     */
    @Override
    public void execute(RPSAgent agent) {
        if(agent.getServer() != null) {
            agent.log("Arriving at " + agent.getServer().getId() + ", I decided to look around and see if I could find anyone willing to parttake in the tournament I had arranged.");
            List<Agent> agents = agent.getServer().getResidingAgents();
            for(Agent neighbour : agents) {
                if(neighbour != agent && neighbour instanceof RPSAgent) {
                    RPSAgent rps = (RPSAgent)neighbour;
                    if(rps.currentState() instanceof FindWaitState) {
                        agent.log("There I met " + rps + " at the local inn.");
                        agent.log("I asked him to join me.");
                        Message join = new Message(agent, MessageTypes.JOIN);
                        rps.postMessage(join);
                        Message msg  = agent.waitForMessage(rps.getHome(), TIMEOUT);
                        if(msg.getType() == Message.TIMEOUT_MESSAGE) {
                            agent.log("To my dissapointment, he did not answer.");
                            continue;
                        }
                        else if(msg.getType() == MessageTypes.JOIN) {
                            agent.log("He answered me that I should rather join his tournament. So I did.");
                            agent.setState(new PrepareJoinState(rps.getHome(), rps.getBattleServer()));
                            break;
                        }
                        else if(msg.getType() == MessageTypes.OK) {
                            agent.log("He happily answered me: Of course I will join you!");
                            agent.addSlave(rps.getHome());
                        }
                    }
                }
            }

            // Wait for someone else to talk with us.
            if(agent.currentState() == this) {
                agent.setState(new FindWaitState());
            }
        }
        else { //Old thread, die
            agent.setState(null);
        }
    }
}
