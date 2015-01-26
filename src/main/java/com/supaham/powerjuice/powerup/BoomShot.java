package com.supaham.powerjuice.powerup;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.jetbrains.annotations.NotNull;

public class BoomShot extends Powerup {

    public static final String NAME = "boom-shot";
    public static final String BLOCK_MDATA = "boom-shot";
    private List<Entity> tnts = new ArrayList<>();
    private boolean shouldUnregister;
    private boolean registered;

    public BoomShot(@NotNull PowerupManager manager) {
        super(manager, NAME, ChatColor.RED + "Boom Shot",
              "Shoots arrow explosions.",
              "Shoots arrow explosions.",
              new ItemStack(Material.CARROT_ITEM), manager.getProperties().getBoomShot().getDuration());
        ItemMeta m = this.item.getItemMeta();
        m.setDisplayName(getDisplayName());
        this.item.setItemMeta(m);
    }

    @Override
    public void addUser(@NotNull Player player) {
        if (!this.getUsers().contains(player)) {
            this.getUsers().add(player);
            if (!registered) {
                plugin.getLog().finer("Registering powerup " + this.getClass().getName() + " events...");
                plugin.regEvents(this);
                registered = true;
            }
            onUserAdd(player);
        }
    }

    @Override
    public boolean removeUser(@NotNull Player player) {
        if (getUsers().remove(player)) {
            // We need to listen for the EntityChangeBlockEvent
            if (!tnts.isEmpty()) {
                shouldUnregister = true;
            } else {
                System.out.println("Unregistering...");
                unregister();
            }
            onUserRemove(player);
            return true;
        }
        return false;
    }

    private void unregister() {
        plugin.getLog().finer("Unregistering powerup " + getClass().getName() + " events...");
        HandlerList.unregisterAll(this);
        registered = false;
        shouldUnregister = false;
    }

    @Override
    public void onUserAdd(Player player) {
    }

    @Override
    public void onUserRemove(Player player) {
    }

    public PowerupProperties.BoomShot getProperties() {
        return manager.getProperties().getBoomShot();
    }

    @EventHandler
    public void onEntityShootBow(EntityShootBowEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        if (!isUser(((Player) event.getEntity()))) {
            return;
        }
        event.setCancelled(true);
        Projectile proj = ((Projectile) event.getProjectile());
        FallingBlock falling = proj.getWorld().spawnFallingBlock(proj.getLocation(), Material.TNT, (byte) 0);
        falling.setVelocity(proj.getVelocity());
        falling.setMetadata(BLOCK_MDATA, new FixedMetadataValue(plugin, proj.getShooter()));
        falling.setDropItem(false);
        proj.remove();
        tnts.add(falling);
    }

    @EventHandler
    public void onEntityChangeBlock(EntityChangeBlockEvent event) {
        Entity falling = event.getEntity();
        if (!falling.hasMetadata(BLOCK_MDATA)) {
            return;
        }
        event.setCancelled(true);
        falling.getWorld().createExplosion(falling.getLocation(), 0F);
        double r = getProperties().getKillRange();
        falling.getNearbyEntities(r, r, r).stream().filter(e -> {
            return e instanceof Player && this.plugin.getGameManager().getSession(((Player) e)).isAlive();
        })
                .forEach(e -> ((Player) e).damage(20, ((Entity) falling.getMetadata(BLOCK_MDATA).get(0).value())));
        falling.remove();
        tnts.remove(falling);
        if (shouldUnregister && tnts.isEmpty()) {
            unregister();
        }
    }
}
