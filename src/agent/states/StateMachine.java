package agent.states;

/**
 * A general purpose state machine.
 * @author Daniel Abrahamsson
 */
public class StateMachine<T> implements java.io.Serializable {
    private final T owner;
    private State<T> currentState;
    private State<T> globalState;

    /**
     * Initializes the state machine.
     * @param owner The owner of the state machine, which states will operate upon.
     */
    public StateMachine(T owner) {
        this.owner = owner;
    }

    /**
     * Executes the statemachine.
     */
    public void execute() {
        if(globalState != null) globalState.execute(owner);
        if(currentState != null) currentState.execute(owner);
    }

    /**
     * Returns the global state.
     * @return the global state
     */
    public State<T> getGlobalState() {
        return globalState;
    }

    /**
     * Returns the current state.
     * @return the current state.
     */
    public State<T> getCurrentState() {
        return currentState;
    }

    /**
     * Sets the owner's state.
     * @param state The new state
     */
    public void setState(State<T> state) {
        if(state != null) {
            if(currentState != null)
                currentState.exit(owner);
            state.enter(owner);
            currentState = state;
        }
    }

    /**
     * Sets the global state.
     * @param state The new global state.
     */
    public void setGlobalState(State<T> state) {
        if(state != null) {
            if(globalState != null)
                globalState.exit(owner);
            state.enter(owner);
            globalState = state;
        }
    }
}
