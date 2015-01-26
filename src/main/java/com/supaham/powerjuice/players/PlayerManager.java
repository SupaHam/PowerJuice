package com.supaham.powerjuice.players;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.supaham.powerjuice.PowerJuicePlugin;
import com.supaham.powerjuice.game.Game;
import com.supaham.powerjuice.game.GamerSession;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import lombok.Data;

/**
 * Represents a manager for {@link PJPlayer}s.
 */
@Data
public class PlayerManager {

    private final PowerJuicePlugin plugin;
    private final Map<String, PJPlayer> pJPlayers = new HashMap<>();

    /**
     * Converts a {@link Collection} of {@link PJPlayer}s into a {@link List} of {@link Player}s.
     *
     * @param pjPlayers pjPlayers list to convert.
     * @return the new {@link List} of {@link Player}s.
     */
    public static List<Player> pjPlayersToPlayers(@NotNull Collection<PJPlayer> pjPlayers) {
        return pjPlayers.stream().map(PJPlayer::getPlayer).collect(Collectors.toList());
    }
    
    /**
     * Converts a {@link Collection} of {@link PJPlayer}s into a {@link List} of unignored {@link Player}s.
     *
     * @param pjPlayers pjPlayers list to convert.
     * @return the new {@link List} of {@link Player}s.
     */
    public static List<Player> unignoredPlayers(@NotNull Collection<PJPlayer> pjPlayers) {
        return pjPlayers.stream().filter(t -> !t.isIgnored()).map(PJPlayer::getPlayer).collect(Collectors.toList());
    }

    /**
     * Converts a {@link Collection} of {@link PJPlayer}s into a {@link List} of unignored {@link Player}s.
     *
     * @param pjPlayers pjPlayers list to convert.
     * @return the new {@link List} of {@link Player}s.
     */
    public static List<PJPlayer> unignoredPJPlayers(@NotNull Collection<PJPlayer> pjPlayers) {
        return pjPlayers.stream().filter(t -> !t.isIgnored()).collect(Collectors.toList());
    }

    public PlayerManager(PowerJuicePlugin plugin) {
        this.plugin = plugin;
    }

    public PJPlayer createPJPlayer(Player player) {
        PJPlayer pjPlayer = new PJPlayer(player);
        addPlayer(pjPlayer);
        return pjPlayer;
    }

    public PJPlayer getPJPlayer(@NotNull CommandSender sender) {
        return getPJPlayer(sender.getName());
    }

    public PJPlayer getPJPlayer(@NotNull String playerName) {
        return this.pJPlayers.get(playerName.toLowerCase());
    }

    public void addPlayer(@NotNull PJPlayer pjPlayer) {
        addPlayer(pjPlayer.getName(), pjPlayer);
    }

    private void addPlayer(@NotNull String playerName, @NotNull PJPlayer pjPlayer) {
        this.pJPlayers.put(playerName.toLowerCase(), pjPlayer);
    }

    public PJPlayer removePlayer(@NotNull Player player) {
        return removePlayer(player.getName());
    }

    public PJPlayer removePlayer(@NotNull String playerName) {
        PJPlayer pjPlayer = getPJPlayer(playerName);
        if (pjPlayer != null) { // If the player has a GamerSession, notify the session that the player has quit.
            Game currGame = plugin.getGameManager().getCurrentGame();
            if (currGame != null) {
                GamerSession session = currGame.getSession(pjPlayer.getPlayer());
                if (session != null) session.quit();
            }
            pjPlayer.quit();
            this.pJPlayers.remove(playerName.toLowerCase()); // I don't remove straight away in case other code tries 
                                                             // to get the PJPlayer 
        }
        return pjPlayer;
    }
}
