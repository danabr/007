package agent.messaging;

import agent.AbstractAgent;
import agent.discovery.Peer;
import java.net.InetAddress;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Abstract class for agents with messaging capabilities.
 * @author Daniel Abrahamsson
 */
public abstract class MessagingAgent extends AbstractAgent {
    /** Contains incoming messages */
    protected final BlockingQueue<Message> messageQueue;

    /**
     * Initializes the messageing agent.
     * @param clientAddress Home IP of the agent
     * @param clientPort Home port of the agent
     */
    public MessagingAgent(InetAddress clientAddress, int clientPort) {
        super(clientAddress, clientPort);
        messageQueue = new LinkedBlockingQueue<Message>();
    }

    /**
     * Called when the agent is about to migrate.
     * Still not processed messages from the current server
     * are removed.
     */
    @Override
    public void onMigrate() {
        messageQueue.clear();
    }

    /**
     * Posts the given message to the agent's message queue.
     * @param msg The message to be posted
     */
    public void postMessage(Message msg) {
        messageQueue.add(msg);
    }

    /**
     * Waits at most for a message from the given peer.
     * Messages from other peers are discarded.
     * @param sender The specific sender to wait for messages from.
     * @param timeout The maximum amount of time to wait
     * @return A message from the peer, or a special timeout message.
     */
    public Message waitForMessage(Peer sender, int timeout) {
        long end = System.currentTimeMillis() + timeout;
        while(System.currentTimeMillis() <= end) {
            try
            {
                Message msg = messageQueue.poll(end - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
                if(msg != null && msg.getSender().getHome().equals(sender))
                    return msg;
            }
            catch(InterruptedException ie) {}
        }
        return new Message(this, Message.TIMEOUT_MESSAGE);
    }

    /**
     * Waits for a message of the given type.
     * All other kinds of messages are discarded.
     * @param msgType The type of message to wait for
     * @param timeout The maximum amount of time to wait
     * @return A message
     */
    public Message waitForMessage(int msgType, int timeout) {
        long end = System.currentTimeMillis() + timeout;
        while(System.currentTimeMillis() <= end) {
            try
            {
                Message msg = messageQueue.poll(end - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
                if(msg != null && msg.getType() == msgType)
                    return msg;
            }
            catch(InterruptedException ie) {}
        }
        return new Message(this, Message.TIMEOUT_MESSAGE);
    }
}
