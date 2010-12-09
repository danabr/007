package rps.statistics;

import agent.discovery.Peer;
import java.io.Serializable;
import java.util.List;
import rps.util.Tuple;

/**
 * Holds tournament data.
 * @author Daniel Abrahamsson
 */
public class Tournament implements Serializable {
        /**
         * A scoreboard entry.
         */
        public class ScoreboardEntry implements Serializable {
            public final Peer player;
            public final int score;

            /**
             * Initializes a scorboard entry.
             * @param player The player
             * @param score His score
             */
            public ScoreboardEntry(Peer player, int score) {
                this.player = player;
                this.score = score;
            }
        }
        private final Peer server;
        private final long tournamentEnd;
        private final List<ScoreboardEntry> scoreboard;

        /**
         * Initializes a tournament.
         * @param server The server the tournament was held at.
         * @param scoreboard Tournament scoreboard.
         */
        public Tournament(Peer server, List<Tuple<Peer, Integer>> scoreboard) {
            this.server = server;
            this.tournamentEnd = System.currentTimeMillis();
            this.scoreboard = new java.util.ArrayList<ScoreboardEntry>(scoreboard.size());
            for(Tuple<Peer, Integer> entry : scoreboard)
                this.scoreboard.add(new ScoreboardEntry(entry.a, entry.b));
        }

        /**
         * Returns the tournament's scoreboard.
         * @return The tournament's scoreboard.
         */
        public List<ScoreboardEntry> getScoreboard() {
            return scoreboard;
        }

        /**
         * Returns the server the tournament was held at.
         * @return The server the tournament was held at.
         */
        public Peer getServer() {
            return server;
        }

        /**
         * Returns the time the tournament ended.
         * @return The time the tournament ended.
         */
        public long getTournamentTime() {
            return tournamentEnd;
        }
    }
