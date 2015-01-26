package com.supaham.powerjuice.game;

import com.supaham.powerjuice.PowerJuicePlugin;
import com.supaham.powerjuice.events.game.GameStopEvent.Reason;
import com.supaham.powerjuice.events.game.GamerMoveEvent;
import com.supaham.powerjuice.events.game.gamersession.GamerPointsChangeEvent;
import com.supaham.powerjuice.powerup.Powerup;
import com.supaham.powerjuice.util.BlockUtil;
import com.supaham.powerjuice.util.Language;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.minecart.StorageMinecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class GameListener implements Listener {

    private final Game game;
    private PowerJuicePlugin plugin;

    public GameListener(@NotNull Game game) {
        this.game = game;
        this.plugin = game.getPlugin();
    }

    private GamerSession get(Player player) {
        return get(player, true);
    }

    private GamerSession get(Player player, boolean inGame) {
        GamerSession session = game.getSession(player);
        if (session == null) {
            return null;
        }
        return inGame && !session.isPlaying() ? null : session;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        System.out.println("Joining...");
        game.addSession(plugin.getPlayerManager().getPJPlayer(event.getPlayer())).setup();
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        GamerSession session = get(event.getPlayer(), false);
        if (session != null) {
            session.quit();
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (event.isCancelled()) return;

        GamerSession session = get(event.getPlayer());
        if (session != null) {
            GamerMoveEvent gamerMoveEvent = new GamerMoveEvent(session, event);
            Bukkit.getPluginManager().callEvent(gamerMoveEvent);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onBlockBreak(BlockBreakEvent event) {
        GamerSession session = get(event.getPlayer());
        if (session == null || session.getPJPlayer().isIgnored()) {
            return;
        }
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onBlockPlace(BlockPlaceEvent event) {
        GamerSession session = get(event.getPlayer());
        if (session == null || session.getPJPlayer().isIgnored()) {
            return;
        }
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerInteract(PlayerInteractEvent event) {
        GamerSession session = get(event.getPlayer());
        if (session == null || session.getPJPlayer().isIgnored()) {
            return;
        }

        if (event.hasBlock() && BlockUtil.isContainer(event.getClickedBlock().getType())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onGamerBalanceChange(GamerPointsChangeEvent event) {
        int goal = game.getProperties().getPointsGoal();
        if (goal > 0 && event.getNewBalance() >= goal) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    game.getManager().stop(Reason.GOAL_REACHED);
                }
            }.runTask(plugin);
        }
    }

    @EventHandler
    public void powerupPickup(VehicleDestroyEvent event) {
        if (!(event.getVehicle() instanceof StorageMinecart)) {
            return;
        }
        Entity damager = event.getAttacker();
        if (!(damager instanceof Player)) {
            return;
        }
        GamerSession session = get(((Player) damager));
        if (session == null) {
            return;
        }
        event.setCancelled(true);
        givePowerup(event.getVehicle(), (Player) damager);
    }

    @EventHandler
    public void powerupPickup(PlayerInteractEntityEvent event) {
        if (!event.getRightClicked().getType().equals(EntityType.MINECART_CHEST)) {
            return;
        }
        Player player = event.getPlayer();
        GamerSession session = get(player);
        if (session == null) {
            return;
        }
        event.setCancelled(true);

        givePowerup(event.getRightClicked(), player);
    }

    private void givePowerup(Entity entity, Player player) {
        GamerSession session = get(player);
        if (session.getPJPlayer().isIgnored() || session.isSpecatating()) {
            return;
        }

        Location loc = entity.getLocation();
        Powerup powerup = game.getPowerupManager().getRandomPowerup();
        powerup.give(player);

        game.getPowerupManager().spawnRandomPowerup(game.getProperties().getPowerupLocations(), entity);

        entity.remove();
    }

    @EventHandler
    public void powerupConsume(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        GamerSession session = get(player);
        if (session == null || session.getPJPlayer().isIgnored()) {
            return;
        }
        if (session.getCurrentPowerup() != null) {
            event.setCancelled(true);
            Language.Game.POWERUP_ALREADY_ACTIVE.send(player, session.getCurrentPowerup().getDisplayName());
            return;
        }
        ItemStack item = event.getItem();
        Powerup powerup = game.getPowerupManager().getPowerupByItemStack(item);
        if (powerup != null) {
            powerup.addUser(player);
            session.setCurrentPowerup(powerup);
        }
    }

    @EventHandler
    public void playerDeath(PlayerDeathEvent event) {
        GamerSession session = get(event.getEntity());
        event.getDrops().clear();
        event.setDroppedExp(0);
        session.die();
        if (game.getStorm().getVictims().contains(event.getEntity())) {
            event.setDeathMessage(event.getEntity().getDisplayName() + " died to the storm.");
        }
    }

    @EventHandler
    public void playerRespawn(PlayerRespawnEvent event) {
        GamerSession session = get(event.getPlayer());
        if (session == null) {
            return;
        }
        session.spawn(event);
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        Player player = (Player) event.getEntity();
        if (event.getCause().equals(EntityDamageEvent.DamageCause.FALL)) {
            event.setCancelled(true);
        }
        GamerSession session = get(player);
        if (session != null) {
            session.launch();
        }
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        Player victim = (Player) event.getEntity();
        Player damager = event.getDamager() instanceof Player ? (Player) event.getDamager() : null;
        if (damager == null) {
            if (!(event.getDamager() instanceof Projectile)) {
                return;
            }
            ProjectileSource shooter = ((Projectile) event.getDamager()).getShooter();
            if (shooter instanceof Player) {
                damager = (Player) shooter;
            } else {
                return;
            }
        }
        GamerSession victimSession = get(victim);
        GamerSession damagerSession = get(damager);
        if (victimSession == null || damagerSession == null || victim.equals(damager) || !damagerSession.isAlive()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void arrowHit(ProjectileHitEvent event) {
        if (event.getEntity() instanceof Arrow) {
            event.getEntity().remove();
        }
    }

    @EventHandler
    public void playerSneak(PlayerToggleSneakEvent event) {
        if (!event.isSneaking()) {
            return;
        }
        Player player = event.getPlayer();
        GamerSession session = get(player);
        if (session == null || session.getLastSneakLaunch() > 0) {
            return;
        }
        player.setVelocity(new Vector(0, -2, 0));
        player.playSound(player.getLocation().subtract(0, 10, 0), Sound.FIREWORK_LAUNCH, 2F, 0.5F);
        session.setLastSneakLaunch(game.getProperties().getSneakLaunchCD());
    }
    
    /* 
     * ================================
     * |        MISC LISTENERS        |
     * ================================ 
     */

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        GamerSession session = get(((Player) event.getEntity()));
        if (session == null) {
            return;
        }
        event.setFoodLevel(19);
        ((Player) event.getEntity()).setSaturation(20L);
    }
}
