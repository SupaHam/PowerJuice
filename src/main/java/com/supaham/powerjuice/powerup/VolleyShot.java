package com.supaham.powerjuice.powerup;

import com.supaham.powerjuice.util.NumberUtil;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

public class VolleyShot extends Powerup {

    public static final String NAME = "volley-shot";

    public VolleyShot(@NotNull PowerupManager manager) {
        super(manager, NAME, ChatColor.AQUA + "Volley Shot",
              "Fire multiple arrows in the direction you're looking at.",
              "Fire multiple arrows in the direction you're looking at.",
              new ItemStack(Material.COOKED_BEEF), manager.getProperties().getVolleyShot().getDuration());
        ItemMeta m = this.item.getItemMeta();
        m.setDisplayName(this.displayName);
        this.item.setItemMeta(m);
    }

    @Override
    public void onUserAdd(Player player) {
    }

    @Override
    public void onUserRemove(Player player) {
    }

    public PowerupProperties.VolleyShot getProperties() {
        return manager.getProperties().getVolleyShot();
    }

    @EventHandler
    public void onProjectileLaunch(EntityShootBowEvent event) {
        Entity shooter = event.getEntity();
        if (!(shooter instanceof Player)) {
            return;
        }
        Player player = ((Player) shooter);

        if (!isUser(player)) {
            return;
        }
        Entity proj = event.getProjectile();
        int shots = getProperties().getShots() + (NumberUtil.nextInt(2) + 1);
        float speed = getProperties().getSpeed() * event.getForce();
        float spread = getProperties().getSpread();
        
        for (int i = 0; i < shots; i++) {
            Arrow arrow = player.getWorld().spawnArrow(proj.getLocation(), proj.getVelocity(), speed, spread);
            arrow.setVelocity(arrow.getVelocity());
            arrow.setShooter(player);
        }
    }
}
