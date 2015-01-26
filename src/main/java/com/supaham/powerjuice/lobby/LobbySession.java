package com.supaham.powerjuice.lobby;

import java.lang.ref.WeakReference;
import java.util.UUID;

import com.supaham.powerjuice.PowerJuicePlugin;
import com.supaham.powerjuice.game.Game;
import com.supaham.powerjuice.players.PJPlayer;
import lombok.AccessLevel;
import lombok.Getter;
import me.confuser.barapi.BarAPI;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.potion.PotionEffect;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a Session belonging to a {@link PJPlayer}.
 */
@Getter
public class LobbySession {

    private final PowerJuicePlugin plugin;
    private final LobbyManager lobbyManager;

    private final UUID owner;
    @Getter(AccessLevel.NONE)
    private boolean quit;

    private WeakReference<PJPlayer> pjPlayer;

    protected static LobbySession createSession(@NotNull LobbyManager lobbyManager, @NotNull PJPlayer pjPlayer) {
        PowerJuicePlugin.getInstance().getLog().finer("Creating LobbySession for " + pjPlayer.getName());
        return new LobbySession(lobbyManager, pjPlayer);
    }

    private LobbySession(LobbyManager lobbyManager, PJPlayer owner) {
        this.lobbyManager = lobbyManager;
        this.plugin = lobbyManager.getPlugin();

        this.owner = owner.getPlayer().getUniqueId();
        this.pjPlayer = new WeakReference<>(owner);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{"
               + "owner=" + owner
               + ",quit=" + quit
               + "}";
    }

    public PJPlayer getPJPlayer() {
        PJPlayer pjPlayer = this.pjPlayer.get();
        if (pjPlayer == null) {
            Player player = Bukkit.getPlayer(owner);
            if (player != null) {
                this.pjPlayer = new WeakReference<>(plugin.getPlayerManager().getPJPlayer(player));
                pjPlayer = this.pjPlayer.get();
            }
        }
        return pjPlayer;
    }

    public void setup() {
        plugin.getLog().finer("Setting up " + this + ".");
        quit = false;
        PJPlayer pjPlayer = getPJPlayer();
        Validate.notNull(pjPlayer, "pjPlayer cannot be null when setting up GamerSession.");
        spawn();
    }

    public void reset() {
        Player player = getBukkitPlayer();
        BarAPI.removeBar(player);
        player.setFoodLevel(20);
        player.setMaxHealth(20D);
        player.setHealth(20D);
        player.getInventory().clear();
        player.getEquipment().clear();
    }

    public void spawn() {
        spawn(null);
    }

    public void spawn(PlayerRespawnEvent event) {
        plugin.getLog().finer("Spawning " + this + ".");
        Player player = getPJPlayer().getPlayer();
        if (event != null) {
            event.setRespawnLocation(player.getLocation());
            return;
        }

        player.getInventory().clear();
        player.getEquipment().clear();

        player.setTicksLived(1);
        player.setAllowFlight(false);
        if(!player.getGameMode().equals(GameMode.CREATIVE)) {
            player.setFlying(false);
            player.setFlySpeed(0.1F);
        }
        // Players exp is set when the game is counting down
        player.setExp(0F);
        player.setLevel(0);
        
        player.setFoodLevel(20);
        player.setSaturation(20);
        player.setMaxHealth(20D);
        player.setHealth(20D);
        for (PotionEffect effect : player.getActivePotionEffects()) {
            player.removePotionEffect(effect.getType());
        }
        
        player.teleport(lobbyManager.getRandomSpawn());
    }

    public void quit() {
        Player player = getBukkitPlayer();
        player.setMaxHealth(20D);
        player.setHealth(20D);
        quit = true;
    }

    /**
     * Checks whether this {@link LobbySession} is active. A case where the [@link LobbySession} wouldn't be active
     * is when the player is playing in a {@link Game}.
     *
     * @return whether this {@link LobbySession}'s owner is playing the game.
     */
    public boolean isActive() {
        PJPlayer pjPlayer = getPJPlayer();
        return pjPlayer != null && !pjPlayer.isQuitting();
    }

    public boolean isOnline() {
        return !quit;
    }

    public String getFriendlyName() {
        return owner + "(" + getPJPlayer().getName() + ")";
    }
    
    /* 
     * ================================
     * |      DELEGATE METHODS        |
     * ================================ 
     */

    public Player getBukkitPlayer() {
        PJPlayer pjPlayer = getPJPlayer();
        if (pjPlayer == null) {
            return null;
        }
        return pjPlayer.getPlayer();
    }

    public String getName() {
        return getBukkitPlayer().getName();
    }

    public Location getLocation() {
        return getBukkitPlayer().getLocation();
    }
}
