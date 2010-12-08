package rps;

/**
 * Contains message types used by RPS agents
 * @author Daniel
 */
public interface MessageTypes {
    /** Request to join tournament. */
    public final static int JOIN            = 1;
    /** Accept message */
    public final static int OK              = 2;
    /** Order to go home. */
    public final static int GO_HOME         = 3;
    /** Indicates message containing a match schema. */
    public final static int MATCHLIST       = 4;
    /** Indicates that an agent can start playing tournament matches */
    public final static int START           = 5;
    /** Ask/Tell if/that an agent is ready to play a game. */
    public final static int READY           = 6;
    /** Tell agent to send his move. */
    public final static int GO              = 7;
    /** Contains match results. */
    public final static int MATCH_RESULTS   = 8;
    /** Contains move in Rock/Paper/Scissors game. */
    public final static int PLAY            = 9;
    /** Contains tournament statistics */
    public final static int STATISTICS      = 10;
}
