package com.supaham.powerjuice.game;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import me.confuser.barapi.BarAPI;

class GameBossBar {

    private Game game;

    public GameBossBar(@NotNull Game game) {
        this.game = game;
    }

    protected void tick() {
        long startTime = game.getProperties().getStartTime();
        long total = game.getProperties().getEndTime() - startTime;
        long elapsed = System.currentTimeMillis() - startTime;
        float health = 100 - Math.round((elapsed * 100) / total);
        for (GamerSession session : game.getPlayingSessions()) {
            Player player = session.getBukkitPlayer();
            if (!BarAPI.hasBar(player)) {
                BarAPI.setMessage(player, ChatColor.YELLOW + "Time remaining");
            }
            BarAPI.setHealth(player, health);
        }
    }
}
