package com.supaham.powerjuice.weapon;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.supaham.powerjuice.game.GameProperties.WeaponProperties;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class SuperBow extends Weapon {

    public static final int BOOST_COOLDOWN = 20;
    private Map<Player, Long> lastBoosted = new HashMap<>();

    /**
     * {@inheritDoc}
     */
    public SuperBow(@NotNull WeaponManager weaponManager) {
        super(weaponManager, "superbow", "SuperBow",
                "Rek kids with your SuperBow.",
                "Rek kids with your SuperBow.",
                new ItemStack(Material.BOW), 0);
        ItemMeta m = this.item.getItemMeta();
        m.setDisplayName(ChatColor.AQUA + "SuperBow");
        m.setLore(Arrays.asList(summary));
        m.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
        m.addEnchant(Enchantment.ARROW_DAMAGE, 10, true);
        this.item.setItemMeta(m);
    }

    @Override
    public void onUserAdd(Player player) {
    }

    @Override
    public void onUserRemove(Player player) {
        lastBoosted.remove(player);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (!event.getAction().name().contains("LEFT") || !canUse(player)) {
            return;
        }
        Long last = lastBoosted.get(player);
        if (last != null && ((System.currentTimeMillis() - last) / 50) <= BOOST_COOLDOWN) {
            return;
        }

        // This feature only works in the air.
        if (!player.getLocation().subtract(0, 1, 0).getBlock().getType().equals(Material.AIR)) {
            return;
        }
        WeaponProperties.SuperBow superBow =
                plugin.getGameManager().getCurrentGame().getProperties().getWeapons().getSuperBow();
        Vector dir = player.getEyeLocation().getDirection()
                .multiply(superBow.getVelocityMultiplier()).setY(superBow.getYVelocity());
        player.setVelocity(dir);
        player.getWorld().playSound(player.getLocation(), Sound.FIREWORK_LAUNCH, 2F, 1.5F);
        lastBoosted.put(player, System.currentTimeMillis());
    }

    @EventHandler
    public void shootArrow(EntityShootBowEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        Player player = (Player) event.getEntity();
        new BukkitRunnable() {
            @Override
            public void run() {
                player.getItemInHand().setDurability((short) 0);
                player.updateInventory();
            }
        }.runTask(plugin);
    }
}
