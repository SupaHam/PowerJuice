package com.supaham.powerjuice.scoreboard;

import java.util.ArrayList;
import java.util.List;

import com.supaham.powerjuice.PowerJuicePlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a scoreboard that can be assigned to {@link Player}s.
 */
public class PJScoreboard {

    protected final PowerJuicePlugin plugin;

    private Scoreboard scoreboard;
    private final List<Player> viewers = new ArrayList<>();

    public PJScoreboard(@NotNull PowerJuicePlugin plugin) {
        this.plugin = plugin;
        this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
    }

    /**
     * Gets the {@link Scoreboard} belonging to this {@link PJScoreboard}.
     *
     * @return bukkit Scoreboard
     */
    public Scoreboard getScoreboard() {
        return scoreboard;
    }

    public List<Player> getViewers() {
        return viewers;
    }

    /**
     * Clears this scoreboard.
     */
    public void clear() {
        for (int i = 0; i < viewers.size(); i++) {
            removePlayer(viewers.get(i));
        }
    }

    /**
     * Adds a {@link Player} to the list of viewers.
     *
     * @param player player to add
     */
    protected void addPlayer(@NotNull Player player) {
        if (!this.viewers.contains(player)) {
            boolean result = this.viewers.add(player);
            plugin.getLog().finest("%s removed from %s: %s", player.getName(), this, result);
        }
        player.setScoreboard(getScoreboard());
    }

    /**
     * Removes a {@link Player} from the list of viewers.
     *
     * @param player player to remove
     * @return whether the player was removed
     */
    protected boolean removePlayer(@NotNull Player player) {
        if (player.getScoreboard().equals(getScoreboard())) {
            player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
        }
        return this.viewers.remove(player);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + viewers.toString();
    }
}
