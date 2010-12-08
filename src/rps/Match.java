package rps;

import agent.discovery.Peer;

/**
 * Represents a Rock/Paper/Scissors match.
 * @author Daniel Abrahamsson
 */
public class Match {
    public final static int NO_MOVE         = 0;
    /** ROCK move */
    public final static int ROCK_MOVE       = 1;
    /** Paper move */
    public final static int PAPER_MOVE      = 2;
    /** Scissors move */
    public final static int SCISSORS_MOVE   = 3;

    /** The judge */
    public final Peer judge;
    /** First participant */
    public final Peer participant1;
    /** Second participant */
    public final Peer participant2;
    private int participant1Score;
    private int participant2Score;

    /**
     * Initializes a match.
     * @param judge The judge of the match
     * @param participant1 The first participant of the match
     * @param participant2 The second participant of the match
     */
    public Match(Peer judge, Peer participant1, Peer participant2) {
        this.judge = judge;
        this.participant1 = participant1;
        this.participant2 = participant2;
        this.participant1Score = this.participant2Score = 0;
    }

    /**
     * Retutrns the winner of the game.
     * @return The winner of the game, or null if the game has not been finished.
     */
    public Peer getWinner() {
        if(!isDone()) return null;
        return (participant1Score > participant2Score) ? participant1 : participant2;
    }

    /**
     * Returns true if the match is done.
     * @return true if the match is done.
     */
    public boolean isDone() {
        return participant1Score >= 3 || participant2Score >= 3;
    }

    /**
     * Finds out whether the given peer takes part in the match.
     * @param peer 
     * @return true if the given peer is taking part in the match.
     */
    public boolean isTakingPart(Peer peer) {
        return judge.equals(peer) || participant1.equals(peer) || participant2.equals(peer);
    }

    /**
     * Plays a move.
     * @param p1Move First player move
     * @param p2Move Second player move
     */
    public void play(int p1Move, int p2Move) {
        int result = resultOf(p1Move, p2Move);
        if(result == 1)
            participant1Score++;
        else if(result == -1)
            participant2Score++;
    }

    /**
     * Returns the current score of the game.
     * @return The current score of the game.
     */
    public String score() {
        return participant1Score + " - " + participant2Score;
    }

    /**
     * A string representation of the match.
     * @return A string representation of the match.
     */
    @Override
    public String toString() {
        return participant1 + " vs " + participant2 + ", judged by " + judge;
    }

    /**
     * Returns the string representation of the move.
     * @param move The move value
     * @return A string representation of the move
     */
    public static String moveToString(int move) {
        switch(move) {
            case NO_MOVE:
                return "no move at all";
            case ROCK_MOVE:
                return "rock";
            case PAPER_MOVE:
                return "paper";
            case SCISSORS_MOVE:
                return "scissors";
            default:
                return "an unknown move";
        }
    }

    /**
     * Returns 1 if p1Move beats p2Move, 0 if it is a draw, and -1 if it loses.
     * @param p1Move First player move
     * @param p2Move Second player move
     * @return The result of the moves.
     */
    private int resultOf(int p1Move, int p2Move) {
        int[][] winTable = {{0, -1, -1, -1}, //NO_MOVE
                            {1, 0, -1, 1},   //ROCK_MOVE
                            {1, 1, 0, -1},   //PAPER_MOVE
                            {1, -1, 1, 0}};  //SCISSOR_MOVE
        return winTable[p1Move][p2Move];
    }
}
