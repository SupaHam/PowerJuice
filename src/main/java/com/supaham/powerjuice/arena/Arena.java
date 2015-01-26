package com.supaham.powerjuice.arena;

import java.util.List;
import java.util.Map;

import com.supaham.powerjuice.PowerJuicePlugin;
import com.supaham.powerjuice.platform.PlatformProperties;
import com.supaham.powerjuice.util.LocationUtil;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pluginbase.minecraft.location.Coordinates;
import pluginbase.minecraft.location.FacingCoordinates;

/**
 * Represents an Arena that contain the following:
 * <p/>
 * <ul>
 * <li>name</li>
 * <li>description</li>
 * <li>boundaries</li>
 * <li>spawns</li>
 * </ul>
 */
public class Arena implements Comparable<Arena> {

    private PowerJuicePlugin plugin = PowerJuicePlugin.getInstance();

    @Getter
    private ArenaManager arenaManager;

    @Getter
    private ArenaProperties properties;

    public Arena(@NotNull ArenaManager arenaManager, @NotNull ArenaProperties properties) {
        this.arenaManager = arenaManager;
        this.properties = properties;
    }

    public void save() {
        this.arenaManager.saveArenaProperties(properties);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Arena)) {
            return false;
        }

        Arena other = (Arena) obj;
        return other.getName().equals(getName());
    }

    @Override
    public int compareTo(@NotNull Arena o) {
        return getName().compareTo(o.getName());
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{min=" + getMinimumPoint().toString() + ", " +
               "max=" + getMaximumPoint().toString() + "}";
    }

    public boolean contains(@NotNull Location location) {
        return contains(LocationUtil.locationToCoords(location));
    }

    public boolean contains(@NotNull Coordinates coords) {
        return LocationUtil.isWithin(coords, getMinimumPoint(), getMaximumPoint());
    }

    @NotNull
    public String getName() {
        return properties.getName();
    }

    public String getDisplayName() {
        return getDisplayName(false);
    }
    
    /**
     * Gets this {@link Arena}'s Display name.
     *
     * @param useNameIfNull whether to use the name if the display name is null or empty.
     * @return Display name
     */
    public String getDisplayName(boolean useNameIfNull) {
        String name = properties.getDisplayName();
        if (useNameIfNull && (name == null || name.isEmpty())) {
            return properties.getName();
        }
        return name;
    }

    public void setDisplayName(String displayName) {
        properties.setDisplayName(ChatColor.translateAlternateColorCodes('&', displayName));
    }

    public String getDescription() {
        return getDescription(false);
    }
    
    public String getDescription(boolean defaultIfNull) {
        String desc = properties.getDescription();
        if(defaultIfNull && (desc == null || desc.isEmpty())) {
            return "No description";
        }
        return desc;
    }

    public void setDescription(String description) {
        properties.setDescription(ChatColor.translateAlternateColorCodes('&', description));
    }

    public String getAuthors() {
        return properties.getAuthors();
    }

    public void setAuthors(String authors) {
        properties.setAuthors(ChatColor.translateAlternateColorCodes('&', authors));
    }

    public Coordinates getMinimumPoint() {
        return properties.getMin();
    }

    public Coordinates getMaximumPoint() {
        return properties.getMax();
    }

    public void setBoundaries(@NotNull Coordinates min, @NotNull Coordinates max) {
        properties.setBoundaries(min, max);
    }

    public List<FacingCoordinates> getSpawns() {
        return properties.getSpawns();
    }

    /**
     * Checks whether this {@link Arena} has a {@link Location} (only cares about block coordinates).
     *
     * @param location location to check
     * @return whether this {@link Arena} has the spawn
     */
    public boolean hasSpawn(@NotNull Location location) {
        return hasSpawn(LocationUtil.locationToCoords(location));
    }

    /**
     * Checks whether this {@link Arena} has a {@link Coordinates}.
     *
     * @param coordinates coordinates to check
     * @return whether this {@link Arena} has that spawn
     */
    public boolean hasSpawn(@NotNull Coordinates coordinates) {
        return fuzzySpawnMatch(coordinates) != null;
    }

    /**
     * Tries to find a spawn point match of a {@link Location}.
     *
     * @param location location to match
     * @return the matched {@link FacingCoordinates} if found, otherwise null
     */
    @Nullable
    public FacingCoordinates fuzzySpawnMatch(@NotNull Location location) {
        return fuzzySpawnMatch(LocationUtil.locationToCoords(location));
    }

    /**
     * Tries to find a spawn point match of a {@link Coordinates}.
     *
     * @param coordinates coordinates to match
     * @return the matched {@link FacingCoordinates} if found, otherwise null
     */
    @Nullable
    public FacingCoordinates fuzzySpawnMatch(@NotNull Coordinates coordinates) {

        List<FacingCoordinates> spawns = getSpawns();
        if (spawns.isEmpty()) {
            return null;
        }

        for (FacingCoordinates spawn : spawns) {
            if (LocationUtil.sameBlock(spawn, coordinates)) return spawn;
        }
        return null;
    }

    public boolean addSpawn(@NotNull Location location) {
        return addSpawn(LocationUtil.locationToFacingCoords(location));
    }

    public boolean addSpawn(@NotNull FacingCoordinates coordinates) {
        return getSpawns().add(coordinates);
    }

    public boolean removeSpawn(@NotNull Location location) {
        return removeSpawn(LocationUtil.locationToFacingCoords(location));
    }

    public boolean removeSpawn(@NotNull FacingCoordinates coordinates) {
        return getSpawns().remove(coordinates);
    }

    public List<Coordinates> getPowerupLocations() {
        return properties.getPowerups();
    }

    /**
     * Checks whether this {@link Arena} has a powerup at a {@link Location}.
     *
     * @param location location to check
     * @return whether this {@link Arena} has that powerup location
     */
    public boolean hasPowerupLocation(@NotNull Location location) {
        return hasPowerupLocation(LocationUtil.locationToCoords(location));
    }

    /**
     * Checks whether this {@link Arena} has a powerup at a {@link Coordinates}.
     *
     * @param coordinates coordinates to check
     * @return whether this {@link Arena} has that powerup location
     */
    public boolean hasPowerupLocation(@NotNull Coordinates coordinates) {
        return fuzzyPowerupLocationMatch(coordinates) != null;
    }

    /**
     * Tries to find a powerup location match for a {@link Location}.
     *
     * @param location location to match
     * @return the matched {@link Coordinates} if found, otherwise null
     */
    @Nullable
    public Coordinates fuzzyPowerupLocationMatch(@NotNull Location location) {
        return fuzzyPowerupLocationMatch(LocationUtil.locationToCoords(location));
    }

    /**
     * Tries to find a powerup location match for a {@link Coordinates}.
     *
     * @param coordinates coordinates to match
     * @return the matched {@link Coordinates} if found, otherwise null
     */
    @Nullable
    public Coordinates fuzzyPowerupLocationMatch(@NotNull Coordinates coordinates) {

        List<Coordinates> locs = getPowerupLocations();
        if (locs.isEmpty()) {
            return null;
        }

        for (Coordinates spawn : locs) {
            if (LocationUtil.sameBlock(spawn, coordinates)) return spawn;
        }
        return null;
    }

    public boolean addPowerupLocation(@NotNull Location location) {
        return addPowerupLocation(LocationUtil.locationToCoords(location));
    }

    public boolean addPowerupLocation(@NotNull Coordinates coordinates) {
        return getPowerupLocations().add(coordinates);
    }

    public boolean removePowerupLocation(@NotNull Location location) {
        return removePowerupLocation(LocationUtil.locationToCoords(location));
    }

    public boolean removePowerupLocation(@NotNull Coordinates coordinates) {
        return getPowerupLocations().remove(coordinates);
    }
    
    public Map<String, PlatformProperties> getPlatforms() {
        return getProperties().getPlatforms();
    }
    
    public PlatformProperties getPlatform(@NotNull String platform) {
        return getPlatforms().get(platform.toLowerCase());
    }
    
    public boolean hasPlatform(String platformName) {
        return platformName != null && getPlatforms().containsKey(platformName.toLowerCase());
    }

    public boolean addPlatform(@NotNull PlatformProperties properties) {
        return getPlatforms().put(properties.getName(), properties) != null;
    }

    @Nullable
    public PlatformProperties removePlatform(@NotNull String name) {
        return getPlatforms().remove(name);
    }
}
