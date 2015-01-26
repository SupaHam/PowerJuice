package com.supaham.powerjuice.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.supaham.powerjuice.PowerJuicePlugin;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import lombok.Getter;
import pluginbase.minecraft.location.Coordinates;

public abstract class LocationChecker extends BukkitRunnable {
    private final Vector min;
    private final Vector max;
    @Getter
    private final Map<Player, Integer> outOfBounders = new HashMap<>();
    private int graceTime;

    public LocationChecker(@NotNull Coordinates min, @NotNull Coordinates max) {
        this(LocationUtil.coordsToVector(min), LocationUtil.coordsToVector(max), 5);
    }

    public LocationChecker(@NotNull Vector min, @NotNull Vector max) {
        this(min, max, 5);
    }

    public LocationChecker(@NotNull Coordinates min, @NotNull Coordinates max, int graceTime) {
        this(LocationUtil.coordsToVector(min), LocationUtil.coordsToVector(max), graceTime);
    }

    public LocationChecker(@NotNull Vector min, @NotNull Vector max, int graceTime) {
        this.min = min;
        this.max = max;
        this.graceTime = graceTime + 1;
    }

    public abstract List<Player> getPlayers();

    public void start() {
        runTaskTimer(PowerJuicePlugin.getInstance(), 0, 20);
    }

    public void timesUp(@NotNull Player player) {
        player.setHealth(0D);
    }

    public void warn(@NotNull Player player, int remainingTime) {
        Language.OOB_WARN.send(player, remainingTime);
        player.playSound(player.getLocation(), Sound.NOTE_BASS, 1F, 0.5F);
    }

    public void back(@NotNull Player player) {
        Language.OOB_BACK.send(player);
        player.playSound(player.getLocation(), Sound.NOTE_PLING, 1F, 1.5F);
    }

    @Override
    public void run() {
        for (Player player : getPlayers()) {
            if (player == null) {
                continue;
            }
            if (!player.isOnline() || player.isDead()) {
                outOfBounders.remove(player);
                continue;
            }

            // Player is out of bounds
            if (!LocationUtil.isWithin(player.getLocation().toVector(), min, max)) {
                Integer time = outOfBounders.get(player);
                if (time == null) time = graceTime;
                time--;
                outOfBounders.put(player, time);

                if (time == 0) {
                    timesUp(player);
                    outOfBounders.remove(player);
                    continue;
                }

                warn(player, time);
            } else {
                boolean back = outOfBounders.remove(player) != null;
                if (back) {
                    back(player);
                }
            }
        }
    }
}
