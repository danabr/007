package rps;

import agent.AgentServer;
import agent.messaging.MessagingAgent;
import agent.discovery.Peer;
import java.io.Serializable;
import java.net.InetAddress;
import java.util.List;
import java.util.Set;
import rps.states.GlobalState;

/**
 * Agent playing Rock/Paper/Scissors
 * @author Daniel Abrahamsson
 */
public class RPSAgent extends MessagingAgent implements Serializable {
    private final agent.states.StateMachine<RPSAgent> stateMachine;
    private Peer battleServer;
    private Peer master;
    private final Set<Peer> slaves;
    private final List<String> history;

    /**
     * Insantiates an RPS agent
     * @param clientAddress Home address
     * @param clientPort Home port
     */
    public RPSAgent(InetAddress clientAddress, int clientPort) {
        super(clientAddress, clientPort);
        this.stateMachine = new agent.states.StateMachine<RPSAgent>(this);
        this.stateMachine.setState(new rps.states.InitialState());
        this.stateMachine.setGlobalState(new GlobalState(System.currentTimeMillis() + 30000));
        this.slaves = new java.util.HashSet<Peer>();
        this.history = new java.util.LinkedList<String>();
    }

    /**
     * Adds a slave to the agent.
     * @param slave Identifier of the slave to add.
     */
    public void addSlave(Peer slave) {
        slaves.add(slave);
    }

    /**
     * Returns the current state of the agent.
     * @return The current state of the agent.
     */
    public agent.states.State<RPSAgent> currentState() {
        return stateMachine.getCurrentState();
    }

    /**
     * Returns the server to perform tournaments on.
     * @return The server to perform tournaments on.
     */
    public Peer getBattleServer() {
        return battleServer;
    }

    /**
     * @return The master peer
     */
    public Peer getMaster() {
        return master;
    }

    /**
     * @return The server the agent resides on.
     */
    public AgentServer getServer() {
        return currentServer;
    }

    /**
     * @return A collection of all slaves.
     */
    public Set<Peer> getSlaves() {
        return java.util.Collections.unmodifiableSet(slaves);
    }

    /**
     * Stores the given string in the agent's history
     * @param s The string to be stored
     */
    public void log(String s) {
        history.add(s);
    }

    /**
     * Runs the agent.
     */
    @Override
    public void run() {
        while(currentState() != null && currentServer != null) {
            stateMachine.execute();
        }
    }

    /**
     * Sets the server to battle on.
     * @param battleServer The new battle server
     */
    public void setBattleServer(Peer battleServer) {
        this.battleServer = battleServer;
    }

    /**
     * Sets the master of the peer.
     * @param master The new master of the peer.
     */
    public void setMaster(Peer master) {
        this.master = master;
    }

    /**
     * Update the slave set.
     * @param slaves New slaves
     */
    public void setSlaves(Set<Peer> slaves) {
        this.slaves.clear();
        this.slaves.addAll(slaves);
    }
    
    /**
     * Sets the state of the agent.
     * @param s The new state, or null if you want the client to die.
     */
    public void setState(agent.states.State<RPSAgent> s) {
        stateMachine.setState(s);
    }

    /**
     * Prints the agent's history to standard output.
     */
    public void speakThyTounge() {
        for(String story : history)
            System.out.println(story);
    }
    
    /**
     * @return A string representation of the agent.
     */
    @Override
    public String toString() {
        return "RPSAgent " + getHome();
    }
}
