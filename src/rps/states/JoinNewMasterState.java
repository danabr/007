package rps.states;

import agent.Agent;
import agent.AgentServer;
import agent.messaging.Message;
import rps.MessageTypes;
import rps.RPSAgent;
import agent.discovery.Peer;

/**
 * State for slaves ordered to find a new master.
 * @author Daniel Abrahamsson
 */
public class JoinNewMasterState extends agent.states.State<RPSAgent> {

    /**
     * Executes state logic.
     * @param agent The agent to perform state logic on.
     */
    public void execute(RPSAgent agent) {
        agent.log("Arriving at the new tournament area, I found a seat near the door of the inn so that I would be ready to ask the tournament master if I could join as soon as he arrived.");
        RPSAgent master = null;
        while(master == null) {
            master = findMaster(agent.getServer(), agent.getMaster());
            if(master == null)
                try { Thread.sleep(500); } catch(InterruptedException ie) {}
        }
        agent.log("Finally, he came into the door.");
        Message join = new Message(agent, MessageTypes.JOIN);
        master.postMessage(join);
        Message response = agent.waitForMessage(agent.getMaster(), 5000);
        if(response.getType() == MessageTypes.OK) {
            agent.log("'Of course you can join', he said, smiling at me.");
            agent.log("Happy, I signed the participant list.");
            agent.setState(new SlaveState());
        }
        else { // We came to late. Move home with the sad news
            agent.log("'No, you are to late.' The words rang in my head.");
            agent.log("Truly dissapointed, I decided to go home.");
            agent.setState(new MoveHomeState());
        }

    }

    /**
     * Tries to locate a master agent at the server.
     * @param server Server to look through.
     * @param master The master to find
     * @return The found master, or null.
     */
    public RPSAgent findMaster(AgentServer server, Peer master) {
        for(Agent agent : server.getResidingAgents()) {
            if(agent instanceof RPSAgent && master.equals(agent.getHome()))
                    return (RPSAgent)agent;
        }
        return null;
    }

}
