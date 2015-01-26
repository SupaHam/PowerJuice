package com.supaham.powerjuice.game;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.supaham.powerjuice.PowerJuicePlugin;
import com.supaham.powerjuice.arena.Arena;
import com.supaham.powerjuice.events.game.gamersession.GamerPointsChangeEvent;
import com.supaham.powerjuice.players.PJPlayer;
import com.supaham.powerjuice.powerup.Powerup;
import com.supaham.powerjuice.util.LocationUtil;
import com.supaham.powerjuice.util.MaterialUtil;
import com.supaham.powerjuice.weapon.Weapon;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import me.confuser.barapi.BarAPI;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import static com.supaham.powerjuice.util.Language.Arena.ARENA_INFO_SETUP;

/**
 * Represents a Session belonging to a {@link PJPlayer}.
 */
@Getter
public class GamerSession {

    private final PowerJuicePlugin plugin;

    private final UUID owner;
    private final Game game;
    @Getter(AccessLevel.PRIVATE)
    private final boolean midGame;
    @Getter(AccessLevel.PRIVATE)
    private boolean quit;

    private WeakReference<PJPlayer> pjPlayer;

    private List<Weapon> weapons = new ArrayList<>(); // Weapons this player is using

    private int kills;
    private int deaths;
    private int points;
    private Powerup currentPowerup;

    @Setter(AccessLevel.PROTECTED)
    private int lastSneakLaunch = 0;
    private int powerupTicksLeft = 0;
    private int spectatorTicks = 0;
    private int lastBounce = 0;

    protected static GamerSession createSession(@NotNull PJPlayer pjPlayer, Game game, boolean midGame) {
        PowerJuicePlugin.getInstance().getLog().finer("Creating GamerSession for " + pjPlayer.getName());
        return new GamerSession(pjPlayer, game, midGame);
    }

    private GamerSession(PJPlayer owner, Game game, boolean midGame) {
        this.plugin = game.getPlugin();

        this.owner = owner.getPlayer().getUniqueId();
        this.pjPlayer = new WeakReference<>(owner);
        this.game = game;
        this.midGame = midGame;
        addWeapon(plugin.getWeaponManager().getWeapon("superbow"));
        addWeapon(plugin.getWeaponManager().getWeapon("arrow"));
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{"
               + "owner=" + owner
               + ",kills=" + kills
               + ",deaths=" + deaths
               + ",points=" + points
               + "}";
    }

    public PJPlayer getPJPlayer() {
        PJPlayer pjPlayer = this.pjPlayer.get();
        if (pjPlayer == null) {
            Player player = Bukkit.getPlayer(owner);
            if (player != null) {
                this.pjPlayer = new WeakReference<>(plugin.getPlayerManager().getPJPlayer(player));
                pjPlayer = this.pjPlayer.get();
            }
        }
        return pjPlayer;
    }

    public void setup() {
        plugin.getLog().finer("Setting up " + this + ".");
        quit = false;
        PJPlayer pjPlayer = getPJPlayer();
        Arena arena = game.getArena();
        pjPlayer.send(ARENA_INFO_SETUP, arena.getDisplayName(true), arena.getDescription(true));
        Validate.notNull(pjPlayer, "pjPlayer cannot be null when setting up GamerSession.");
        spawn();
    }

    public void reset() {
        this.kills = 0;
        this.deaths = 0;
        this.points = 0;

        Player player = getBukkitPlayer();
        BarAPI.removeBar(player);
        player.setFoodLevel(20);
        player.setMaxHealth(20D);
        player.setHealth(20D);
        player.getInventory().clear();
        player.getEquipment().clear();
    }

    public void spectate(int ticks) {
        setSpectatorTicks(ticks);
        if (ticks == 0) { // Instant respawn
            return;
        }
        Player player = getBukkitPlayer();
        if (player.isDead()) {
            Location loc = player.getLocation();
            player.spigot().respawn();
            player.teleport(loc);
        }
        player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20, 1, true));
        player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, ticks, 1, true));
        player.setAllowFlight(true);
        player.setFlying(true);
        player.setFlySpeed(0.2F);
        player.spigot().setCollidesWithEntities(false);
        player.setVelocity(player.getVelocity().setY(2));
    }

    public void spawn() {
        spawn(null);
    }

    public void spawn(PlayerRespawnEvent event) {
        Player player = getPJPlayer().getPlayer();
        if (event != null) {
            event.setRespawnLocation(player.getLocation());
            return;
        }

        PlayerInventory inv = player.getInventory();
        inv.clear();
        player.getEquipment().clear();
        plugin.getLog().finer("Equipping up " + this + ".");

        List<Integer> slotsEquipped = new ArrayList<>(); // Minor warning for overridden weapons.
        for (Weapon weapon : weapons) {
            weapon.give(player);
            int slot = weapon.getSlot();
            if (slot > -1) {
                if (slotsEquipped.contains(slot)) {
                    plugin.getLog().warning(getFriendlyName() + "'s slot " + slot + " was overridden by " +
                                            weapon.getName() + ".");
                }
                slotsEquipped.add(slot);
            }
        }
        player.setTicksLived(1);
        player.setAllowFlight(false);
        player.setFlying(false);
        player.setFlySpeed(0.1F);
        player.spigot().setCollidesWithEntities(true);
        player.setFoodLevel(19);
        player.setSaturation(20);
        player.setMaxHealth(2D);
        player.setHealth(2D);
        player.teleport(game.getNextSpawn());
    }

    public void die() {
        setPowerupTicksLeft(0);
        Player player = getBukkitPlayer();

        Player killer = player.getKiller();
        if (killer != null) {
            GamerSession session = game.getSession(killer);
            session.addKill();
            boolean midAir = player.getLocation().subtract(0, 1, 0).getBlock().getType().equals(Material.AIR);
            session.addPoints(midAir ? game.getProperties().getMidAirKillRewardPoints() :
                              game.getProperties().getKillsRewardPoints());
        }

        addDeath();
        new BukkitRunnable() {
            @Override
            public void run() {
                spectate(game.getProperties().getSpectateTicks());
            }
        }.runTask(plugin);
    }

    public void quit() {
        Player player = getBukkitPlayer();
        player.setMaxHealth(20D);
        player.setHealth(20D);
        quit = true;
    }

    protected void tick(boolean secondTick) {
        Player player = getBukkitPlayer();
        if (!isPlaying()) return;
        if (secondTick) {
            if (this.lastBounce >= 20) launch();
        }
        if (this.lastSneakLaunch > 0) setLastSneakLaunch(this.lastSneakLaunch - 1);
        if (this.powerupTicksLeft > 0) setPowerupTicksLeft(this.powerupTicksLeft - 1);
        if (this.spectatorTicks > 0) setSpectatorTicks(this.spectatorTicks - 1);

        this.lastBounce++;
    }

    /**
     * Launches a player based on the block they're standing on.
     */
    public void launch() {
        if (!isAlive()) {
            return;
        }
        Player player = getBukkitPlayer();
        Block b = player.getLocation().getBlock().getRelative(BlockFace.DOWN);
        if (MaterialUtil.same(b, game.getProperties().getBouncyMaterial())) {
            int yDist = LocationUtil.getFirstBlockAbove(player.getLocation()) - player.getLocation().getBlockY();
            double y = yDist >= 30 ? 3 : ((double) yDist / 10);
            player.setVelocity(new Vector(0, y, 0));
        }
        this.lastBounce = 0;
    }

    public boolean isAlive() {
        return this.spectatorTicks <= 0;
    }

    /**
     * Checks whether this {@link GamerSession}'s owner is playing the game. A case where they wouldn't be playing
     * the game is when the player is ignored from the game.
     *
     * @return whether this {@link GamerSession}'s owner is playing the game.
     */
    public boolean isPlaying() {
        PJPlayer pjPlayer = getPJPlayer();
        return pjPlayer != null && !pjPlayer.isQuitting() && !pjPlayer.isIgnored();
    }

    public boolean isOnline() {
        return !quit;
    }

    public String getFriendlyName() {
        return owner + (getPJPlayer() != null ? "(" + getPJPlayer().getName() + ")" : "");
    }

    /**
     * Checks whether this {@link GamerSession} has a {@link Weapon}.
     *
     * @param weapon weapon to check
     * @return whether this {@link GamerSession} has the {@code weapon}
     */
    public boolean hasWeapon(@NotNull Weapon weapon) {
        return this.weapons.contains(weapon);
    }

    public void addWeapon(@NotNull Weapon weapon) {
        plugin.getLog().finer("Adding " + weapon.toString() + " to " + getName() + "...");
        if (!this.weapons.contains(weapon)) {
            this.weapons.add(weapon);
            weapon.addUser(getBukkitPlayer());
        }
    }

    public void removeWeapon(@NotNull Weapon weapon) {
        if (this.weapons.remove(weapon)) {
            weapon.removeUser(getBukkitPlayer());
        }
    }

    /**
     * Adds a single kill to this {@link GamerSession}.
     *
     * @return the new amount of kills
     */
    public int addKill() {
        return setKills(this.kills + 1);
    }

    /**
     * Adds an amount of kills to this {@link GamerSession}.
     *
     * @param amount amount of kills to add
     * @return the new amount of kills
     */
    public int addKills(int amount) {
        Validate.isTrue(amount >= 0, "amount cannot be smaller than 0.");
        return setKills(this.kills + amount);
    }

    /**
     * Subtracts a single kill from this {@link GamerSession}.
     *
     * @return the new amount of kills
     */
    public int subtractKill() {
        return setKills(this.kills - 1);
    }

    /**
     * Subtracts an amount of kills from this {@link GamerSession}.*
     *
     * @param amount amount of kills to subtract
     * @return the new amount of kills
     */
    public int subtractKills(int amount) {
        Validate.isTrue(amount >= 0, "amount can not be smaller than 0.");
        return setKills(this.kills - amount);
    }

    public int setKills(int kills) {
        this.kills = kills;
        return kills;
    }

    /**
     * Adds a single death to this {@link GamerSession}.
     *
     * @return the new amount of deaths
     */
    public int addDeath() {
        return setDeaths(this.deaths + 1);
    }

    /**
     * Adds an amount of deaths to this {@link GamerSession}.*
     *
     * @param amount amount of deaths to add
     * @return the new amount of deaths
     */
    public int addDeaths(int amount) {
        Validate.isTrue(amount >= 0, "amount can not be smaller than 0.");
        return setDeaths(this.deaths + amount);
    }

    /**
     * Subtracts a single death from this {@link GamerSession}. Nothing changes if this session's deaths are already 0.
     *
     * @return the new amount of deaths
     */
    public int subtractDeath() {
        return setDeaths(Math.max(0, this.deaths - 1));
    }

    /**
     * Subtracts an amount of deaths from this {@link GamerSession}.*
     *
     * @param amount amount of deaths to subtract
     * @return the new amount of deaths
     */
    public int subtractDeaths(int amount) {
        Validate.isTrue(amount >= 0, "amount can not be smaller than 0.");
        return setDeaths(Math.max(0, this.deaths - amount));
    }

    public int setDeaths(int deaths) {
        this.deaths = deaths;
        return deaths;
    }

    /**
     * Adds an amount of points to this {@link GamerSession}'s balance.
     *
     * @param amount amount of money to add
     * @return the new amount of points
     */
    public int addPoints(int amount) {
        Validate.isTrue(amount >= 0, "amount can not be smaller than 0.");
        return setPoints(this.points + amount);
    }

    /**
     * Deducts an amount of points from this {@link GamerSession}'s balance.
     *
     * @param amount amount of points to deduct
     * @return the new amount of points
     */
    public int subtractPoints(int amount) {
        Validate.isTrue(amount >= 0, "amount can not be smaller than 0.");
        return setPoints(this.points - amount);
    }

    public int setPoints(int points) {
        GamerPointsChangeEvent event = new GamerPointsChangeEvent(this, this.points, points);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return points;
        }
        this.points = event.getNewBalance();
        game.getScoreboard().updatePoints(this);
        return this.points;
    }

    protected void setCurrentPowerup(Powerup powerup) {
        if (powerup != null) {
            this.powerupTicksLeft = powerup.getDuration() * 20;
        } else if (this.powerupTicksLeft > 0) { // Reset powerup ticks
            setPowerupTicksLeft(0);
        }
        this.currentPowerup = powerup;
    }

    public void setPowerupTicksLeft(int ticks) {
        if (this.powerupTicksLeft > 0 && ticks == 0) {
            getCurrentPowerup().removeUser(getBukkitPlayer());
            this.currentPowerup = null;
        }
        this.powerupTicksLeft = ticks;
    }

    public boolean isSpecatating() {
        return this.spectatorTicks > 0;
    }

    protected void setSpectatorTicks(int spectatorTicks) {
        this.spectatorTicks = spectatorTicks;
        if (spectatorTicks == 0) {
            spawn();
        } else {
            getBukkitPlayer().setTicksLived(1);
        }
    }
    
    /* 
     * ================================
     * |      DELEGATE METHODS        |
     * ================================ 
     */

    public Player getBukkitPlayer() {
        PJPlayer pjPlayer = getPJPlayer();
        if (pjPlayer == null) {
            return null;
        }
        return pjPlayer.getPlayer();
    }

    public String getName() {
        return getBukkitPlayer().getName();
    }

    public Location getLocation() {
        return getBukkitPlayer().getLocation();
    }
}
