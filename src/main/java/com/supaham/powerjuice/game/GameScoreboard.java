package com.supaham.powerjuice.game;

import com.supaham.powerjuice.scoreboard.PJScoreboard;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a {@link PJScoreboard} that is displayed to players in a {@link Game}.
 */
public class GameScoreboard extends PJScoreboard {

    private final Game game;
    private final Objective sidebar;
    private final Objective list;

    public GameScoreboard(@NotNull Game game) {
        super(game.getPlugin());
        this.game = game;

        this.sidebar = getScoreboard().registerNewObjective("game", "dummy");
        this.sidebar.setDisplaySlot(DisplaySlot.SIDEBAR);

        String name = game.getArena().getDisplayName();
        if (name == null || name.isEmpty()) name = game.getArena().getName();
        this.sidebar.setDisplayName(ChatColor.GREEN + "" + ChatColor.BOLD + name);
        
        this.list = getScoreboard().registerNewObjective("list", "dummy");
        this.list.setDisplaySlot(DisplaySlot.PLAYER_LIST);
    }

    @Override
    public void clear() {
        super.clear();
        this.sidebar.unregister();
        this.list.unregister();
    }

    /**
     * Updates this scoreboard.
     */
    public void update() {
    }

    public void updatePoints(GamerSession session) {
        String name = session.getBukkitPlayer().getName();
        int points = session.getPoints();
        this.sidebar.getScore(name).setScore(points);
        this.list.getScore(name).setScore(points);
    }

    /**
     * Adds a {@link GamerSession} to this scoreboard.
     *
     * @param session session to add
     */
    public void addSession(@NotNull GamerSession session) {
        Player player = session.getBukkitPlayer();
        String name = player.getName();
        plugin.getLog().finest("Adding " + name + " to " + toString() + ".");
        this.sidebar.getScore(name).setScore(1);
        this.sidebar.getScore(name).setScore(0);
        addPlayer(player);
    }

    /**
     * Removes a {@link GamerSession} from this scoreboard.
     *
     * @param session session to remove
     */
    public void removeSession(@NotNull GamerSession session) {
        plugin.getLog().finer("Removing " + session.getFriendlyName() + " from " + toString() + ".");
        Player player = session.getBukkitPlayer();
        if (player == null) {
            return;
        }
        removePlayer(player);
    }
}
