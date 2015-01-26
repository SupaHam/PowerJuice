package com.supaham.powerjuice.lobby;

import com.supaham.powerjuice.PowerJuicePlugin;
import com.supaham.powerjuice.players.PJPlayer;
import com.supaham.powerjuice.util.BlockUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.jetbrains.annotations.NotNull;

public class LobbyListener implements Listener {
    private final LobbyManager manager;
    private PowerJuicePlugin plugin;

    public LobbyListener(@NotNull LobbyManager manager) {
        this.manager = manager;
        this.plugin = manager.plugin;
    }

    private PJPlayer get(Player player) {
        return get(player, true);
    }

    private PJPlayer get(Player player, boolean inLobby) {
        PJPlayer pjPlayer = plugin.getPlayerManager().getPJPlayer(player);
        return inLobby && !manager.isInLobby(pjPlayer) ? null : pjPlayer;
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onBlockBreak(BlockBreakEvent event) {
        PJPlayer pjPlayer = get(event.getPlayer());
        if (pjPlayer == null || pjPlayer.isIgnored()) {
            return;
        }
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onBlockPlace(BlockPlaceEvent event) {
        PJPlayer pjPlayer = get(event.getPlayer());
        if (pjPlayer == null || pjPlayer.isIgnored()) {
            return;
        }
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerInteract(PlayerInteractEvent event) {
        PJPlayer pjPlayer = get(event.getPlayer());
        if (pjPlayer == null || pjPlayer.isIgnored()) {
            return;
        }

        if (event.hasBlock() && BlockUtil.isContainer(event.getClickedBlock().getType())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onEntityDamage(EntityDamageEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void OnPlayerDropItem(PlayerDropItemEvent event) {
        PJPlayer pjPlayer = get(event.getPlayer());
        if (pjPlayer == null || pjPlayer.isIgnored()) {
            return;
        }
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        PJPlayer pjPlayer = get(event.getEntity());
        if (pjPlayer == null) {
            return;
        }
        event.setDeathMessage(null);
        event.getDrops().clear();
        event.setDroppedExp(0);
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Lobby lobby = manager.getLobby();
        PJPlayer pjPlayer = get(event.getPlayer());
        if (pjPlayer == null) {
            return;
        }

        if (lobby != null) {
            event.setRespawnLocation(manager.getRandomSpawn());
        }
    }
    
    /* 
     * ================================
     * |        MISC LISTENERS        |
     * ================================ 
     */

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        PJPlayer pjPlayer = get(((Player) event.getEntity()));
        if (pjPlayer == null) {
            return;
        }
        event.setFoodLevel(20);
        ((Player) event.getEntity()).setSaturation(20F);
    }
}
