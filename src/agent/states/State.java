package agent.states;

/**
 * Represents the state of an object.
 * This particular implementation does nothing.
 * All states must be serializable.
 * @param <T> The owner to operate on.
 * @author Daniel Abrahamsson
 */
public class State<T> implements java.io.Serializable {
    
    /**
     * Called when the owner enters the state.
     * This implementation does nothing.
     * @param owner
     */
    public void enter(T owner) {}

    /**
     * Executes state logic.
     * This implementation does nothing.
     * @param owner The entity to perform state logic on.
     */
    public void execute(T owner) {}

    /**
     * Called when the owner is leaving the state.
     * This implementation does nothing.
     * @param owner The entity leaving the state
     */
    public void exit(T owner) {}
}
