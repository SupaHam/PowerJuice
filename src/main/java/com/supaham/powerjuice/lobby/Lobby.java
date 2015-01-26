package com.supaham.powerjuice.lobby;

import java.util.List;

import com.supaham.powerjuice.PowerJuicePlugin;
import com.supaham.powerjuice.util.LocationUtil;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pluginbase.minecraft.location.Coordinates;
import pluginbase.minecraft.location.FacingCoordinates;

/**
 * Represents a Lobby.
 */
public class Lobby {

    private PowerJuicePlugin plugin = PowerJuicePlugin.getInstance();

    @Getter
    private LobbyManager lobbyManager;

    @Getter
    private LobbyProperties properties;

    public Lobby(@NotNull LobbyManager lobbyManager, @NotNull LobbyProperties properties) {
        this.lobbyManager = lobbyManager;
        this.properties = properties;
    }

    public void save() {
        this.lobbyManager.saveLobbyProperties(properties);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Lobby && this.lobbyManager.getLobby() == obj;
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

    public Coordinates getMinimumPoint() {
        return properties.getMin();
    }

    public Coordinates getMaximumPoint() {
        return properties.getMax();
    }

    public void setBoundaries(@NotNull Coordinates min, @NotNull Coordinates max) {
        properties.setBoundaries(min, max);
    }

    public String getAuthors() {
        return properties.getAuthors();
    }

    public void setAuthors(String authors) {
        properties.setAuthors(ChatColor.translateAlternateColorCodes('&', authors));
    }

    public List<FacingCoordinates> getSpawns() {
        return properties.getSpawns();
    }

    /**
     * Checks whether this {@link Lobby} has a {@link Location} (only cares about block coordinates).
     *
     * @param location location to check
     * @return whether this {@link Lobby} has the spawn
     */
    public boolean hasSpawn(@NotNull Location location) {
        return hasSpawn(LocationUtil.locationToCoords(location));
    }

    /**
     * Checks whether this {@link Lobby} has a {@link Coordinates}.
     *
     * @param coordinates coordinates to check
     * @return whether this {@link Lobby} has that spawn
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
}
