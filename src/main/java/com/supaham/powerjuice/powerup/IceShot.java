package com.supaham.powerjuice.powerup;

import com.supaham.powerjuice.events.game.gamersession.GamerGiveWeaponEvent;
import com.supaham.powerjuice.weapon.Arrow;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import static com.supaham.powerjuice.util.NumberUtil.nextInt;

public class IceShot extends Powerup {

    public static final String NAME = "ice-shot";
    private static final String INSTA_KILL_METADATA = "INSTAKILL";

    public IceShot(@NotNull PowerupManager manager) {
        super(manager, NAME, ChatColor.AQUA + "Ice Shot",
              "Fire multiple snowballs in the direction you're looking at.",
              "Fire multiple snowballs in the direction you're looking at.",
              new ItemStack(Material.COOKED_CHICKEN), manager.getProperties().getIceShot().getDuration());
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

    public PowerupProperties.IceShot getProperties() {
        return manager.getProperties().getIceShot();
    }

    @EventHandler
    public void onGamerGiveWeapon(GamerGiveWeaponEvent event) {
        if (!isUser(event.getGamerSession().getBukkitPlayer())) {
            return;
        }

        if (event.getWeapon().getName().equals(Arrow.NAME)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        ProjectileSource shooter = event.getEntity().getShooter();
        if (!(shooter instanceof Player)) {
            return;
        }
        Player player = ((Player) shooter);

        if (!isUser(player)) {
            return;
        }
        event.getEntity().remove();

        Location location = player.getLocation();
        int shots = getProperties().getShots();
        double accuracy = getProperties().getAccuracy();

        double dir = -location.getYaw() - 90.0F;
        double pitch = -location.getPitch();
        for (int i = 0; i < shots; i++) {
            double xwep = (nextInt((int) (accuracy * 100.0D)) - nextInt((int) (accuracy * 100.0D)) + 0.5D) / 100.0D;
            double ywep = (nextInt((int) (accuracy * 100.0D)) - nextInt((int) (accuracy * 100.0D)) + 0.5D) / 100.0D;
            double zwep = (nextInt((int) (accuracy * 100.0D)) - nextInt((int) (accuracy * 100.0D)) + 0.5D) / 100.0D;
            double xd = Math.cos(Math.toRadians(dir)) * Math.cos(Math.toRadians(pitch)) + xwep;
            double yd = Math.sin(Math.toRadians(pitch)) + ywep;
            double zd = -Math.sin(Math.toRadians(dir)) * Math.cos(Math.toRadians(pitch)) + zwep;
            Vector v = new Vector(xd, yd, zd);
            Snowball snowball = (Snowball) player.getWorld().spawnEntity(player.getEyeLocation(), EntityType.SNOWBALL);
            snowball.setVelocity(v);
            snowball.setShooter(player);
            snowball.setMetadata(INSTA_KILL_METADATA, new FixedMetadataValue(this.plugin, null));
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        if (event.getDamager().hasMetadata(INSTA_KILL_METADATA)) {
            event.getDamager().removeMetadata(INSTA_KILL_METADATA, this.plugin);
            event.setDamage(1000D);
        }
    }
}
