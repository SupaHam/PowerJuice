package com.supaham.powerjuice.powerup;

import com.supaham.powerjuice.events.game.gamersession.GamerGiveWeaponEvent;
import com.supaham.powerjuice.weapon.Arrow;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

public class RapidFire extends Powerup {

    public static final String NAME = "rapid-fire";

    public RapidFire(@NotNull PowerupManager manager) {
        super(manager, NAME, ChatColor.RED + "Rapid Fire",
              "Old school bow shooting.",
              "Old school bow shooting.",
              new ItemStack(Material.COOKIE), manager.getProperties().getRapidFire().getDuration());
        ItemMeta m = this.item.getItemMeta();
        m.setDisplayName(getDisplayName());
        this.item.setItemMeta(m);
    }

    @Override
    public void onUserAdd(Player player) {
        plugin.getWeaponManager().getWeapon("arrow").removeWeapon(player);
    }

    @Override
    public void onUserRemove(Player player) {
        plugin.getWeaponManager().getWeapon("arrow").give(player);
    }

    @EventHandler
    public void onGamerGiveWeapon(GamerGiveWeaponEvent event) {
        if(!isUser(event.getGamerSession().getBukkitPlayer())) {
            return;
        }
        
        if (event.getWeapon().getName().equals(Arrow.NAME)) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if(!isUser(player) || !player.getItemInHand().getType().equals(Material.BOW) ||
                !event.getAction().name().startsWith("RIGHT")) {
            return;
        }
        player.launchProjectile(org.bukkit.entity.Arrow.class);
    }
}
