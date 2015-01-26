package com.supaham.powerjuice.powerup;

import java.util.ArrayList;
import java.util.List;

import com.supaham.powerjuice.PowerJuicePlugin;
import com.supaham.powerjuice.events.game.GameStopEvent;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a powerup.
 */
@Getter
public abstract class Powerup implements Listener {

    protected final PowerupManager manager;
    protected final PowerJuicePlugin plugin;
    protected final String name;
    protected final String displayName;
    protected final String summary;
    protected final String description;
    protected final int duration;
    protected ItemStack item;

    private final List<Player> users = new ArrayList<>();

    public Powerup(@NotNull PowerupManager manager, @NotNull String name, @NotNull String displayName,
                   @NotNull String summary, @NotNull String description, @NotNull ItemStack item, int duration) {
        this.manager = manager;
        this.plugin = manager.plugin;
        this.name = name.toLowerCase();
        this.displayName = displayName;
        this.summary = summary;
        this.description = description;
        this.duration = duration;
        this.item = item;
    }

    public void give(@NotNull Player player) {
        player.getInventory().addItem(this.item);
    }

    public boolean isUser(@NotNull Player player) {
        return this.users.contains(player);
    }

    public void addUser(@NotNull Player player) {
        if (!this.users.contains(player)) {
            this.users.add(player);
            if (this.users.size() == 1) { // We got our first user, lets register this powerup's events.
                plugin.getLog().finer("Registering powerup " + this.getClass().getName() + " events...");
                plugin.regEvents(this);
            }
            onUserAdd(player);
        }
    }

    public boolean removeUser(@NotNull Player player) {
        if (this.users.remove(player)) { // Lets not listen to powerups that aren't being used.
            plugin.getLog().finer("Unregistering powerup " + getClass().getName() + " events...");
            HandlerList.unregisterAll(this);
            onUserRemove(player);
            return true;
        }
        return false;
    }

    public boolean canUse(Player player) {
        return this.users.contains(player) && isPowerupItem(player.getItemInHand());
    }

    public boolean isPowerupItem(@Nullable ItemStack item) {
        return item != null && item.isSimilar(this.item); // TODO improve this method to not depend on item name etc. 
    }

    /**
     * This method is called when a new {@link Player} is added to this {@link Powerup}'s users.
     *
     * @param player new user
     */
    public abstract void onUserAdd(Player player);

    /**
     * This method is called when a {@link Player} is removed from this {@link Powerup}'s users.
     *
     * @param player removed user
     */
    public abstract void onUserRemove(Player player);

    @EventHandler
    public final void onPlayerQuit(PlayerQuitEvent event) {
        removeUser(event.getPlayer());
    }

    @EventHandler
    public final void onGameStop(GameStopEvent event) {
        ArrayList<Player> players = new ArrayList<>(this.users);
        players.forEach(this::removeUser);
    }
}
