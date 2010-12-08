package rps.states;

import rps.util.Tuple;
import agent.Agent;
import agent.messaging.Message;
import rps.MessageTypes;
import rps.RPSAgent;
import agent.discovery.Peer;
import java.util.Set;

/**
 * This state is only reachable by master agents.
 * @author Daniel Abrahamsson
 */
public class PrepareJoinState extends agent.states.State<RPSAgent> {
    //How long to wait for slaves.
    private static final int WAIT_TIME = 3000;

    private Peer master;
    private Peer battleServer;

    /**
     * Initializes the state.
     * @param master The new master server
     * @param battleServer The new battle server
     */
    public PrepareJoinState(Peer master, Peer battleServer)
    {
        this.master = master;
        this.battleServer = battleServer;
    }

    /**
     * Executes state logic.
     * @param agent The agent to perform state logic on.
     */
    @Override
    public void execute(RPSAgent agent) {
        Peer oldBattleServer = agent.getBattleServer();
        agent.setMaster(master);
        agent.setBattleServer(battleServer);
        
        Set<Peer> slaves = agent.getSlaves();
        if(!slaves.isEmpty()) {
            agent.log("Since I had arranged my own tournament, I had to tell the participants to join the other tournament.");
            agent.log("So I travelled to my old tournament area at " + oldBattleServer);
            agent.migrate(oldBattleServer);
            if(agent.getServer() != null) { //For the migrated part
                try { Thread.sleep(WAIT_TIME); } catch(InterruptedException ie) {}
                for(Agent neighbour : agent.getServer().getResidingAgents()) {
                    if(slaves.contains(neighbour.getHome())) {
                        Message msg = new Message(agent, MessageTypes.JOIN, new Tuple<Peer, Peer>(master, battleServer));
                        ((RPSAgent)neighbour).postMessage(msg);
                    }
                }
            }
        }

        if(agent.getServer() != null) {
            agent.setState(new SlaveState());
            agent.migrate(battleServer);
            agent.log("I hurried to " + battleServer + ", where my master had told me to wait.");
        }
    }
}
