package com.supaham.powerjuice.players;

import com.supaham.powerjuice.PowerJuicePlugin;
import com.supaham.powerjuice.game.GameManager;
import com.supaham.powerjuice.language.Message;
import com.supaham.powerjuice.lobby.LobbyManager;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a PowerJuice Player.
 */
@Getter
public class PJPlayer {

    private final PowerJuicePlugin plugin = PowerJuicePlugin.getInstance();
    private final Player player;

    private boolean quitting;

    private boolean ignored;

    public PJPlayer(@NotNull Player player) {
        this.player = player;
    }

    public void send(@NotNull String message, Object... args) {
        player.sendMessage(String.format(message, args));
    }

    public void send(@NotNull Message message, Object... args) {
        message.send(player, args);
    }

    /**
     * Gets the {@link Player} that owns this {@link PJPlayer}.
     *
     * @return {@link Player} instance.
     */
    @NotNull
    public Player getPlayer() {
        return player;
    }

    /**
     * Checks whether this {@link PJPlayer} is playing in a game.
     *
     * @return whether this player is playing
     * @see GameManager#isPlaying(PJPlayer)
     */
    public boolean isPlaying() {
        return this.plugin.getGameManager().isPlaying(this);
    }

    public void ignore() {
        LobbyManager mgr = plugin.getLobbyManager();
        if (mgr.isActive()) {
            mgr.getLocationChecker().getOutOfBounders().remove(this.player);
        }
        this.ignored = true;
    }

    public void unignore() {
        this.ignored = false;
    }

    public void quit() {
        this.quitting = true;
    }
    
    /* 
     * ================================
     * |      DELEGATE METHODS        |
     * ================================ 
     */

    public String getName() {
        return player.getName();
    }
}
