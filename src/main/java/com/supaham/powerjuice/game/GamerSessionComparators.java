package com.supaham.powerjuice.game;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang.Validate;
import org.jetbrains.annotations.NotNull;

import static com.supaham.powerjuice.game.GamerSessionComparators.GamerSessionComparator.LEAST_KILLS;
import static com.supaham.powerjuice.game.GamerSessionComparators.GamerSessionComparator.LEAST_POINTS;
import static com.supaham.powerjuice.game.GamerSessionComparators.GamerSessionComparator.MOST_KILLS;
import static com.supaham.powerjuice.game.GamerSessionComparators.GamerSessionComparator.MOST_POINTS;

/**
 * Represents a {@link GamerSession}
 */
public class GamerSessionComparators {

    /**
     * Sorts a {@link List} of {@link GamerSession}s using the {@link Game} belonging to the given {@link
     * GamerSession} with the given {@link GamerSessionComparator}.
     *
     * @param session    session to get game from
     * @param comparator comparator to use
     * @return the sorted {@link List} of {@link GamerSession}
     * @see GamerSessionComparators#sort(List, GamerSessionComparator)
     */
    public static List<GamerSession> sort(@NotNull GamerSession session, @NotNull GamerSessionComparator comparator) {
        return sort(session.getGame(), comparator);
    }

    /**
     * Sorts a {@link List} of {@link GamerSession}s belonging to the given {@link Game} with the given
     * {@link GamerSessionComparator}.
     *
     * @param game       game to get {@link GamerSession} from
     * @param comparator comparator to use
     * @return the sorted {@link List} of {@link GamerSession}
     * @see GamerSessionComparators#sort(List, GamerSessionComparator)
     */
    public static List<GamerSession> sort(@NotNull Game game, @NotNull GamerSessionComparator comparator) {
        return sort(game.getGamerSessions(), comparator);
    }

    /**
     * Sorts a {@link List} of {@link GamerSession}s with the given {@link GamerSessionComparator}.
     *
     * @param sessions   List of {@link GamerSession}s to sort
     * @param comparator comparator to use
     * @return {@code sessions} for chaining
     * @see GamerSessionComparators#sort(List, GamerSessionComparator)
     */
    public static List<GamerSession> sort(@NotNull List<GamerSession> sessions,
                                          @NotNull GamerSessionComparator comparator) {
        Validate.notEmpty(sessions);
        sessions.sort(comparator);
        return sessions;
    }

    /**
     * Sorts a {@link List} of {@link GamerSession}s by the least amount of points, ascending.
     * <pre>
     *     * SupaHam: 10
     *     * otherRandKid: 40
     *     * randomKid: 80
     * </pre>
     *
     * @param sessions List of {@link GamerSession}s to sort
     * @return {@code sessions} for chaining
     */
    public static List<GamerSession> sortByLeastPoints(@NotNull List<GamerSession> sessions) {
        return sort(sessions, LEAST_POINTS);
    }

    /**
     * Sorts a {@link List} of {@link GamerSession}s by the most amount of points, descending.
     * <pre>
     *     * randomKid: 80
     *     * otherRandKid: 40
     *     * SupaHam: 10
     * </pre>
     *
     * @param sessions List of {@link GamerSession}s to sort
     * @return {@code sessions} for chaining
     */
    public static List<GamerSession> sortByMostPoints(@NotNull List<GamerSession> sessions) {
        return sort(sessions, MOST_POINTS);
    }

    /**
     * Sorts a {@link List} of {@link GamerSession}s by the least amount of kills, ascending.
     * <pre>
     *     * SupaHam: 10
     *     * otherRandKid: 40
     *     * randomKid: 80
     * </pre>
     *
     * @param sessions List of {@link GamerSession}s to sort
     * @return {@code sessions} for chaining
     */
    public static List<GamerSession> sortByLeastKills(@NotNull List<GamerSession> sessions) {
        return sort(sessions, LEAST_KILLS);
    }

    /**
     * Sorts a {@link List} of {@link GamerSession}s by the most amount of kills, descending.
     * <pre>
     *     * randomKid: 80
     *     * otherRandKid: 40
     *     * SupaHam: 10
     * </pre>
     *
     * @param sessions List of {@link GamerSession}s to sort
     * @return {@code sessions} for chaining
     */
    public static List<GamerSession> sortByMostKills(@NotNull List<GamerSession> sessions) {
        return sort(sessions, MOST_KILLS);
    }

    public static abstract class GamerSessionComparator implements Comparator<GamerSession> {

        public static final LeastPoints LEAST_POINTS = new LeastPoints();
        public static final MostPoints MOST_POINTS = new MostPoints();
        public static final LeastKills LEAST_KILLS = new LeastKills();
        public static final MostKills MOST_KILLS = new MostKills();
        
        /**
         * This method modifies a {@link List} of {@link GamerSession} to only one or more players that are equal in
         * this comparator.
         *
         * @param sessions sessions to sort
         * @return the {@code sessions} list, keep in mind its the exact same pointer, this return is for convenience.
         */
        public abstract List<GamerSession> getRelevantSessions(@NotNull List<GamerSession> sessions);
    }

    /**
     * Sorts by the least amount of points, ascending.
     * <pre>
     *     * SupaHam: 10
     *     * otherRandKid: 40
     *     * randomKid: 80
     * </pre>
     */
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class LeastPoints extends GamerSessionComparator {
        @Override
        public int compare(GamerSession o1, GamerSession o2) {
            return o1.getPoints() == o2.getPoints() ? 0 : o1.getPoints() > o2.getPoints() ? 1 : -1;
        }

        @Override
        public List<GamerSession> getRelevantSessions(@NotNull List<GamerSession> sessions) {
            Validate.notEmpty(sessions);
            List<GamerSession> relevant = new ArrayList<>();
            int last = sessions.get(0).getPoints(); // First element would be the least points
            for (GamerSession session : sessions) {
                // If this session has more points than the last, then we got the relevant sessions
                if (last < session.getPoints()) break;
                relevant.add(session);
            }
            return relevant;
        }
    }

    /**
     * Sorts by the most amount of points, descending.
     * <pre>
     *     * randomKid: 80
     *     * otherRandKid: 40
     *     * SupaHam: 10
     * </pre>
     */
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class MostPoints extends GamerSessionComparator {
        @Override
        public int compare(GamerSession o1, GamerSession o2) {
            return o1.getPoints() == o2.getPoints() ? 0 : o1.getPoints() < o2.getPoints() ? 1 : -1;
        }

        @Override
        public List<GamerSession> getRelevantSessions(@NotNull List<GamerSession> sessions) {
            return getRelevantSessions(sessions, true);
        }

        /**
         * Gets the relevant {@link GamerSession}s to this comparator.
         *
         * @param sessions  sessions to sort
         * @param trackZero whether to track players with zero kills
         * @return the sessions list, keep in mind its the exact same pointer, this return is for convenience.
         */
        public List<GamerSession> getRelevantSessions(@NotNull List<GamerSession> sessions, boolean trackZero) {
            Validate.notEmpty(sessions);
            List<GamerSession> relevant = new ArrayList<>();
            int last = sessions.get(0).getPoints(); // First element would be the most points
            for (GamerSession session : sessions) {
                int kills = session.getPoints();
                if (!trackZero && kills == 0) { // We don't want people with no kills to be considered
                    continue;
                }
                // If this session has a less points than the last, then we got the relevant sessions
                if (last > kills) break;
                relevant.add(session);
            }
            return relevant;
        }
    }

    /**
     * Sorts by the least amount of kills, ascending.
     * <pre>
     *     * SupaHam: 10
     *     * otherRandKid: 40
     *     * randomKid: 80
     * </pre>
     */
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class LeastKills extends GamerSessionComparator {
        @Override
        public int compare(GamerSession o1, GamerSession o2) {
            return o1.getKills() == o2.getKills() ? 0 : o1.getKills() > o2.getKills() ? 1 : -1;
        }

        @Override
        public List<GamerSession> getRelevantSessions(@NotNull List<GamerSession> sessions) {
            Validate.notEmpty(sessions);
            List<GamerSession> relevant = new ArrayList<>();
            int last = sessions.get(0).getKills(); // First element would be the least kills
            for (GamerSession session : sessions) {
                // If this session has more kills than the last, then we got the relevant sessions
                if (last < session.getKills()) break;
                relevant.add(session);
            }
            return relevant;
        }
    }

    /**
     * Sorts by the most amount of kills, descending.
     * <pre>
     *     * randomKid: 80
     *     * otherRandKid: 40
     *     * SupaHam: 10
     * </pre>
     */
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class MostKills extends GamerSessionComparator {
        @Override
        public int compare(GamerSession o1, GamerSession o2) {
            return o1.getKills() == o2.getKills() ? 0 : o1.getKills() < o2.getKills() ? 1 : -1;
        }

        @Override
        public List<GamerSession> getRelevantSessions(@NotNull List<GamerSession> sessions) {
            return getRelevantSessions(sessions, true);
        }

        /**
         * Gets the relevant {@link GamerSession}s to this comparator.
         *
         * @param sessions  sessions to sort
         * @param trackZero whether to track players with zero kills
         * @return the sessions list, keep in mind its the exact same pointer, this return is for convenience.
         */
        public List<GamerSession> getRelevantSessions(@NotNull List<GamerSession> sessions, boolean trackZero) {
            Validate.notEmpty(sessions);
            List<GamerSession> relevant = new ArrayList<>();
            int last = sessions.get(0).getKills(); // First element would be the most kills
            for (GamerSession session : sessions) {
                int kills = session.getKills();
                if (!trackZero && kills == 0) { // We don't want people with no kills to be considered
                    continue;
                }
                // If this session has a less kills than the last, then we got the relevant sessions
                if (last > kills) break;
                relevant.add(session);
            }
            return relevant;
        }
    }
}
