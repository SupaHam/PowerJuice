package com.supaham.powerjuice.weapon;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.supaham.powerjuice.PowerJuicePlugin;
import com.supaham.powerjuice.events.game.GameStopEvent;
import com.supaham.powerjuice.events.game.gamersession.GamerGiveWeaponEvent;
import com.supaham.powerjuice.game.GameManager;
import com.supaham.powerjuice.game.GamerSession;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a Weapon a player can have.
 */
@Getter
@Setter
@EqualsAndHashCode
public abstract class Weapon implements Listener {

    protected final PowerJuicePlugin plugin;
    protected final WeaponManager manager;
    protected final String name;
    protected final String displayName;
    protected final String summary;
    protected final String description;
    protected final ItemStack item;
    protected final int slot;

    private final List<Player> users = new ArrayList<>();
    /**
     * This is a list of players that quit the server and that should be readded when they join.
     */
    private final List<UUID> toReadd = new ArrayList<>();

    /**
     * Creates a new Weapon.
     *
     * @param manager     {@link WeaponManager} to manage this item.
     * @param name        hardcoded name of this Weapon.
     * @param displayName the display name, typically the one shown to the user.
     * @param summary     summary of this Weapon.
     * @param description long description of this Weapon.
     * @param item        ItemStack that is this Weapon.
     * @param slot        slot to equip this weapon in, -1 for the first available slot.
     */
    public Weapon(@NotNull WeaponManager manager, @NotNull String name, @NotNull String displayName,
                  @NotNull String summary, @NotNull String description, @NotNull ItemStack item, int slot) {
        this.manager = manager;
        this.plugin = manager.plugin;
        this.name = name.toLowerCase();
        this.displayName = displayName;
        this.summary = summary;
        this.description = description;
        this.item = item;
        this.slot = slot;
    }

    @Override
    public String toString() {
        return toString(this.plugin.getLog().getDebugLevel() < 2);
    }
    
    public String toString(boolean simple) {
        return getClass().getSimpleName() + "{"
               + "manager=" + this.manager.toString(simple)
               + ",name=" + this.name
               + ",displayName=" + this.displayName
               + ",summary=" + this.summary
               + (simple ? "" : ",description=" + this.description)
               + (simple ? "" : ",item=" + this.item)
               + ",slot=" + this.slot
               + "}";
    }
    
    public boolean give(@NotNull Player player) {
        GameManager mgr = plugin.getGameManager();
        if (mgr.hasStarted()) {
            GamerSession session = mgr.getSession(player);
            if (session != null) {
                GamerGiveWeaponEvent event = new GamerGiveWeaponEvent(session, this);
                Bukkit.getPluginManager().callEvent(event);
                if (event.isCancelled()) {
                    return false;
                }
            }
        }
        if (this.slot == -1) {
            player.getInventory().addItem(this.item);
        } else {
            player.getInventory().setItem(this.slot, this.item);
        }
        return true;
    }

    public boolean removeWeapon(@NotNull Player player) {
        PlayerInventory inv = player.getInventory();
        ItemStack item = inv.getItem(slot);
        if (item == null || !item.getType().equals(Material.ARROW)) {
            return false;
        }
        inv.setItem(slot, null);
        return true;
    }

    public boolean isUser(@NotNull Player player) {
        return this.users.contains(player);
    }

    public void addUser(@NotNull Player player) {
        if (!this.users.contains(player)) {
            this.users.add(player);
            if (this.toReadd.isEmpty() && this.users.size() == 1) { // We got our first user, 
            // lets register this weapon's events.
                plugin.getLog().finer("Registering weapon " + this.getClass().getName() + " events...");
                plugin.regEvents(this);
            }
            onUserAdd(player);
        }
    }

    public boolean removeUser(@NotNull Player player) {
        if (this.users.remove(player)) { // Lets not listen to weapons that aren't being used.
            if (this.toReadd.isEmpty()) { // We need to be able to listen to PlayerJoinEvent :P
                plugin.getLog().finer("Unregistering weapon " + getClass().getName() + " events...");
                HandlerList.unregisterAll(this);
            }
            onUserRemove(player);
            return true;
        }
        return false;
    }

    public boolean canUse(Player player) {
        if (!this.users.contains(player)) return false;
        ItemStack held = player.getItemInHand();
        return held != null && held.isSimilar(item); // TODO improve this method to not depend on item name etc.
    }

    /**
     * This method is called when a new {@link Player} is added to this {@link Weapon}'s users.
     *
     * @param player new user
     */
    public abstract void onUserAdd(Player player);

    /**
     * This method is called when a {@link Player} is removed from this {@link Weapon}'s users.
     *
     * @param player removed user
     */
    public abstract void onUserRemove(Player player);

    @EventHandler(ignoreCancelled = true)
    public final void onPlayerDropItem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        if (isUser(player) && player.getInventory().getHeldItemSlot() == this.slot) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public final void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player) || !isUser(((Player) event.getWhoClicked()))) {
            return;
        }
        if (event.getClick().equals(ClickType.NUMBER_KEY) || event.getSlot() == this.slot) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public final void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (this.toReadd.remove(player.getUniqueId())) {
            addUser(player);
        }
    }

    @EventHandler
    public final void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        this.toReadd.add(player.getUniqueId());
        removeUser(player);
    }

    @EventHandler
    public final void onGameStop(GameStopEvent event) {
        ArrayList<Player> players = new ArrayList<>(this.users);
        players.forEach(this::removeUser);
    }
}
