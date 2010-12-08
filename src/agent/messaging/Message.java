package agent.messaging;

/**
 * A message that can be passed between agents.
 * Message types with a value <= 0 are reserved.
 * @author Daniel Abrahamsson
 */
public class Message implements java.io.Serializable {
    /**
     * Message type indicating that a message listening routine has timed out.
     */
    public final static int TIMEOUT_MESSAGE = 0;
    private int type;
    private Object data;
    private MessagingAgent sender;

    /**
     * Creates a message with no data.
     * @param sender The sender of the message.
     * @param type The type of the message
     */
    public Message(MessagingAgent sender, int type) {
        this(sender, type, null);
    }

    /**
     * Initializes a new message.
     * @param sender The sender of the message.
     * @param type The type of the message.
     * @param data The data associated with the message.
     */
    public Message(MessagingAgent sender, int type, Object data) {
        this.sender = sender;
        this.type = type;
        this.data = data;
    }

    /**
     * @return Data associated with the message
     */
    public Object getData() {
        return data;
    }

    /**
     * @return The sender of the message.
     */
    public MessagingAgent getSender() {
        return sender;
    }

    /**
     * Returns the type of the message.
     * @return The type of the message
     */
    public int getType() {
        return type;
    }
}
