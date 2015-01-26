package com.supaham.powerjuice;

import java.util.HashMap;
import java.util.Map;

import com.supaham.powerjuice.game.GameManager;
import com.supaham.powerjuice.game.GameState;
import com.supaham.powerjuice.lobby.LobbyManager;
import com.supaham.powerjuice.players.PJPlayer;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

@RequiredArgsConstructor
class PJListener implements Listener {

    private final PowerJuicePlugin plugin;

    // TODO remove when minecraft fixes its shet
    // This is cute little thing that makes Bukkit's PlayerInteractEvent a little bit smarter.
    private Map<Player, Long> lastInteraction = new HashMap<>();

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerInteractLowest(PlayerInteractEvent event) {
        event.setCancelled(false); // *slap* Get the fuck out Bukkit default cancellation!
        Player player = event.getPlayer();

        long now = System.currentTimeMillis();
        Long last = lastInteraction.put(player, now);
        if (last != null && (now - last) <= 10) { // 10 is an extremely high value (at least locally), the avg. is 2
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        LobbyManager lobbyManager = plugin.getLobbyManager();

        if (lobbyManager.getLobby() == null && !player.hasPermission("pj.join-lobby-null")) {
            player.setMetadata("fkin.rekt", new FixedMetadataValue(plugin, null));
            new BukkitRunnable() {
                @Override
                public void run() {
                    player.kickPlayer("Lobby isn't ready...");
                }
            }.runTask(plugin);
            return;
        }

        PJPlayer pjPlayer = plugin.getPlayerManager().createPJPlayer(player);

        GameManager gameManager = plugin.getGameManager();
        if (gameManager.getState().equals(GameState.STARTED)) {
            gameManager.addSession(pjPlayer);
        } else {
            try {
                lobbyManager.addPlayer(pjPlayer);
            } catch (PJException e) { // Expected to be thrown when the lobby doesn't exist.
                pjPlayer.getPlayer().sendMessage(ChatColor.RED + e.getMessage());
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        lastInteraction.remove(player);

        if(!player.hasMetadata("fkin.rekt")) {
            PJPlayer pjPlayer = plugin.getPlayerManager().removePlayer(player);
            plugin.getLobbyManager().removePlayer(pjPlayer);
        }
    }
    
    @EventHandler
    public void onWeatherChange(WeatherChangeEvent event) {
        if (event.toWeatherState()) {
            event.setCancelled(true);
        }
    }
}
