package com.supaham.powerjuice.powerup;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.supaham.powerjuice.PowerJuicePlugin;
import com.supaham.powerjuice.util.CollectionUtil;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.minecart.StorageMinecart;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pluginbase.bukkit.config.BukkitConfiguration;
import pluginbase.bukkit.config.YamlConfiguration;
import pluginbase.messages.messaging.SendablePluginBaseException;

import static com.supaham.powerjuice.util.LocationUtil.locToString;

/**
 * Represents a {@link Powerup}.
 */
public class PowerupManager {

    protected final PowerJuicePlugin plugin;

    private final Map<String, Powerup> powerups = new HashMap<>();
    private final Map<String, Powerup> displayNamePowerups = new HashMap<>();
    private File powerupsFolder;
    
    @Getter
    private PowerupProperties properties;

    public PowerupManager(PowerJuicePlugin plugin) {
        this.plugin = plugin;
        powerupsFolder = plugin.getDataFolder();
        if (!powerupsFolder.exists()) {
            powerupsFolder.mkdirs();
        }
    }

    public void load() {
        this.properties = loadPowerupProperties();
        addPowerup(new BoomShot(this));
        addPowerup(new IceShot(this));
        addPowerup(new RapidFire(this));
        addPowerup(new Speed(this));
        addPowerup(new VolleyShot(this));
    }

    private File getPowerupsFile() {
        return new File(powerupsFolder, "powerups.yml");
    }
    
    private PowerupProperties loadPowerupProperties() {
        plugin.getLog().fine("Loading powerup properties.");
        File file = getPowerupsFile();
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            YamlConfiguration config = BukkitConfiguration.loadYamlConfig(file);
            PowerupProperties defaults = new PowerupProperties();
            PowerupProperties properties = config.getToObject("settings", defaults);
            if (properties == null) {
                plugin.getLog().fine("Creating defaults powerup properties.");
                properties = defaults;
                config.set("settings", properties);
                config.save(file);
            }
            return properties;
        } catch (IOException | SendablePluginBaseException e) {
            plugin.getLog().severe("Error occurred while getting powerup properties: ");
            e.printStackTrace();
            return null;
        }
    }

    public Powerup getRandomPowerup() {
        return CollectionUtil.getRandomElement(new ArrayList<>(this.powerups.values()));
    }
    
    public void spawnRandomPowerup(@NotNull List<Location> locations, @Nullable Entity last) {

        List<Location> locs = new ArrayList<>(locations);
        Collections.shuffle(locs);
        while (locs.size() > 0) {
            Location curr = locs.remove(0);
            if (last != null && curr.distance(last.getLocation()) <= 2) {
                continue;
            }
            curr = curr.clone().add(0, 1, 0);
            this.plugin.getLog().fine("Spawning powerup at %s", locToString(curr));
            curr.getWorld().spawn(curr, StorageMinecart.class);
            break;
        }
    }
    
    public void clearSpawnedPowerups(@NotNull List<Location> locations) {
        for (Location loc : locations) {
            loc.getWorld().getEntitiesByClass(StorageMinecart.class).stream()
                    .filter(minecart -> loc.distance(minecart.getLocation()) <= 2).forEach(Entity::remove);
        }
    }

    @Nullable
    public Powerup getPowerupByItemStack(@Nullable ItemStack item) {
        if (item == null || !item.hasItemMeta()) {
            return null;
        }
        return getPowerupByDisplayName(item.getItemMeta().getDisplayName());
    }

    /**
     * Gets a {@link Powerup} by display displayName.
     *
     * @param displayName displayName of the {@link Powerup} to get
     * @return powerup
     */
    @Nullable
    public Powerup getPowerupByDisplayName(@Nullable String displayName) {
        if (displayName == null || displayName.isEmpty()) {
            return null;
        }
        return this.displayNamePowerups.get(displayName);
    }

    /**
     * Gets a {@link Powerup} by name. If it fails, it checks by display name.
     *
     * @param name name of the {@link Powerup} to get.
     * @return powerup
     */
    @Nullable
    public Powerup getPowerup(@NotNull String name) {
        Powerup powerup = this.powerups.get(name);
        if (powerup == null) {
            powerup = this.displayNamePowerups.get(name);
        }
        return powerup;
    }

    private void addPowerup(@NotNull Powerup powerup) {
        this.powerups.put(powerup.getName(), powerup);
        if (!powerup.getDisplayName().isEmpty()) {
            this.displayNamePowerups.put(powerup.getDisplayName(), powerup);
        }
    }
}
