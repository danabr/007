package rps.states;

import agent.Footprint;
import rps.RPSAgent;
import agent.discovery.Peer;
import java.util.List;
import java.util.Set;

/**
 * Only master agents can be in this state.
 * @author Daniel Abrahamsson
 */
public class FindContinueState extends agent.states.State<RPSAgent> {
    /**
     * Executes state logic.
     * @param agent The agent to perform state logic on.
     */
    @Override
    public void execute(RPSAgent agent) {
        //Go on until we have found enough tournament participants
        Set<Peer> slaves = agent.getSlaves();
        if(slaves.size() < 2 || Math.random() < 0.1) {
            agent.log("It was time to move on.");
            if(slaves.size() >= 2) //Random
                agent.log("Although I had enough participants to my tournament, I thought I would just visit one place more.");
            agent.log("I had a chat with the host if he knew any nearby places where I could find brave men to join my tournament.");
            //Follow agent's we have not met before
            List<Footprint> footprints = agent.getServer().getFootprints();
            List<Peer> servers = agent.getServer().getNeighbours();
            if(footprints.size() > 0 && servers.size() > 0) {
                long currentTime = System.currentTimeMillis();
                for(Footprint p : footprints) {
                    if(currentTime - p.getTimestamp() < 2000 && //Don't follow old tracks
                            !slaves.contains(p.getHome()) &&    //Only follow potential participants
                            servers.contains(p.getDestination()) && //Must be able to move there
                            !p.getHome().equals(agent.getHome())) { //Don't follow yourself
                        agent.log("'I saw " + p.getHome() + " leave towards " + p.getDestination() + "', the host said, pointing with his thick arm.");
                        agent.log("'He might be willing to join you.'");
                        agent.log("I thanked the host and left the tavern quickly, in hope I would reach " + p.getDestination() + " in time.");
                        agent.setState(new FindState());
                        agent.migrate(p.getDestination());
                        break;
                    }
                }
            }
            if(agent.currentState() == this) { //No one to follow
                if(servers.size() > 0) {
                    //Choose first server not already visited
                    List<Peer> visited = agent.getVisitedServers();
                    for(Peer server : servers) {
                        if(!visited.contains(server)) {
                            agent.log("'You could try " + server + "', the host said.");
                            agent.log("I thanked him and moved on.");
                            agent.setState(new FindState());
                            agent.migrate(server);
                            break;
                        }
                    }
                    if(agent.currentState() == this) {
                        agent.log("But I found out there were no new places to visit.");
                        waitForLatecomers(agent);
                    }
                }
                else { //No server to move to, prepare tournament
                    agent.log("'I'm sorry', the host said. 'Around here is only wilderness.'");
                    agent.log("I decided to give up searching, in hope that some of the participants would have brought their friends with them.");
                    waitForLatecomers(agent);
                }
            }
        }
        else {
            agent.log("As I had enough participants for my tournament, I figured I could just as well get it started.");
            waitForLatecomers(agent);
        }
    }

    /**
     * Moves to the WaitForLatecomers state.
     */
    private void waitForLatecomers(RPSAgent agent) {
        agent.setState(new WaitForLatecomersState());
        agent.migrate(agent.getBattleServer());
    }
}
