package com.supaham.powerjuice.powerup;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

public class Speed extends Powerup {

    public static final String NAME = "speed";

    public Speed(@NotNull PowerupManager manager) {
        super(manager, NAME, ChatColor.AQUA + "Speed",
              "Run faster to dodge your foes' arrows.",
              "Run faster to dodge your foes' arrows.",
              new ItemStack(Material.RAW_BEEF), manager.getProperties().getSpeed().getDuration());
        ItemMeta m = this.item.getItemMeta();
        m.setDisplayName(this.displayName);
        this.item.setItemMeta(m);
    }

    @Override
    public void onUserAdd(Player player) {
        PowerupProperties.Speed speed = getProperties();
        player.addPotionEffect(
                new PotionEffect(PotionEffectType.SPEED, 9999999, speed.getSpeedLevel(), speed.isAmbient()));
    }

    @Override
    public void onUserRemove(Player player) {
        player.removePotionEffect(PotionEffectType.SPEED);
    }

    public PowerupProperties.Speed getProperties() {
        return manager.getProperties().getSpeed();
    }
}
