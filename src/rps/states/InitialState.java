package rps.states;

import rps.RPSAgent;
import agent.discovery.Peer;

/**
 * Initial agent state.
 * @author Daniel Abrahamsson
 */
public class InitialState extends agent.states.State<RPSAgent> {

    /**
     * Executes state logic.
     * @param agent The agent to perform state logic on.
     */
    @Override
    public void execute(RPSAgent agent) {
        Peer battleServer = agent.getServer().getId();
        agent.log("I started my journey at " + battleServer);
        agent.log("This would be an ideal place for a tournament!");
        agent.setBattleServer(battleServer);
        agent.setState(new FindState());
    }

}
