package rps.states;

import rps.util.Tuple;
import agent.messaging.Message;
import rps.Match;
import rps.MessageTypes;
import rps.RPSAgent;
import agent.discovery.Peer;
import java.util.List;

/**
 * Slave state. Slaves waits patiently for messages
 * from their master.
 * @author Daniel Abrahamsson
 */
public class SlaveState extends agent.states.State<RPSAgent> {
    private List<Match> matches;

    /**
     * Called when the owner enters the state.
     * This implementation does nothing.
     * @param owner
     */
    @Override
    public void enter(RPSAgent agent) {
        agent.log("At the tournament area, I patiently sat and waited for the tournament master to address me.");
    }

    /**
     * Executes state logic.
     * @param agent The agent to perform state logic on.
     */
    @Override
    public void execute(RPSAgent agent) {
        Message msg = agent.waitForMessage(agent.getMaster(), 5000);
        if(msg.getType() != Message.TIMEOUT_MESSAGE) {
            agent.log("Suddenly, I heard the tournament master call my name!");
            agent.log("'Here I am, what is it?', I shouted, happily waving my arms so he would see me.");
            if(msg.getType() == MessageTypes.JOIN) {
                handleJoinMessage(agent, msg);
            }
            else if(msg.getType() == MessageTypes.GO_HOME) {
                agent.log("'You better return home', he said, his voice sad.");
                agent.log("'But, why?', I asked.");
                agent.log("'" + msg.getData() + "'");
                agent.setState(new MoveHomeState());
            }
            else if(msg.getType() == MessageTypes.MATCHLIST) {
                this.matches = (List<Match>)msg.getData();
                agent.log("'Here is the tournament schedule', he said, handing me a folder.");
                agent.log("I could see that there would be " + matches.size() + " matches.");
            }
            else if(msg.getType() == MessageTypes.START) {
                agent.log("'The tournament is about to begin. Get ready!'");
                agent.log("I could feel how my heart started to beat faster.");
                agent.setState(new MatchPlayingState(matches));
            }
        }
        else
            agent.log("It was a long wait.");
    }

    /**
     * Processes a join message from the master.
     * @param agent The agent who received the message.
     * @param msg The received message.
     */
    private void handleJoinMessage(RPSAgent agent, Message msg) {
        Tuple<Peer,Peer> data = (Tuple<Peer,Peer>)msg.getData();
        if(!data.a.equals(agent.getMaster())) {
            agent.log("'" + data.a + " is preparing a bigger tournament. Let's go join it instead!'");
            agent.log("So I travelled to " + data.b + " in hope I would be there before the tournament started.");
            agent.setMaster(data.a);
            agent.setBattleServer(data.b);
            agent.setState(new JoinNewMasterState());
            agent.migrate(data.b);
        }
    }
}
