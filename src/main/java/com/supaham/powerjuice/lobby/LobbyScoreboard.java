package com.supaham.powerjuice.lobby;

import com.supaham.powerjuice.arena.Arena;
import com.supaham.powerjuice.scoreboard.PJScoreboard;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a {@link PJScoreboard} that is displayed to players in the {@link Lobby}.
 */
public class LobbyScoreboard extends PJScoreboard {

    private final LobbyManager manager;
    
    private final Objective objective;

    public LobbyScoreboard(@NotNull LobbyManager manager) {
        super(manager.plugin);
        this.manager = manager;
        this.objective = getScoreboard().registerNewObjective("lobby", "dummy");
        this.objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        this.objective.setDisplayName(ChatColor.YELLOW + "Arena votes");
    }

    /**
     * Adds a {@link Player} to this {@link LobbyScoreboard}.
     *
     * @param player player to add
     */
    public void addPlayer(@NotNull Player player) {
        plugin.getLog().finest("Adding " + player.getName() + " to " + toString() + ".");
        super.addPlayer(player);
    }

    /**
     * Removes a {@link Player} from this {@link LobbyScoreboard}.
     *
     * @param player player to remove
     * @return whether the player was removed
     */
    public boolean removePlayer(@NotNull Player player) {
        plugin.getLog().finest("Removing " + player.getName() + " from " + toString() + ".");
        return super.removePlayer(player);
    }

    @Override
    public void clear() {
        super.clear();
        this.objective.unregister();
    }

    /**
     * Updates an {@link Arena}'s votes.
     *
     * @param arena Arena to update
     * @param votes {@code arena}'s votes
     */
    public void updateArenaVotes(Arena arena, Integer votes) {
        this.objective.getScore(ChatColor.AQUA + arena.getName()).setScore(votes);
    }
}
