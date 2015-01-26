package com.supaham.powerjuice.arena;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.supaham.powerjuice.PowerJuicePlugin;

import org.jetbrains.annotations.NotNull;

import lombok.Getter;
import pluginbase.bukkit.config.BukkitConfiguration;
import pluginbase.bukkit.config.YamlConfiguration;
import pluginbase.messages.messaging.SendablePluginBaseException;

/**
 * Represents an {@link Arena} manager.
 */
public class ArenaManager {

    private final PowerJuicePlugin plugin;
    private File arenasFolder;
    private Map<String, ArenaProperties> arenaPropertiesMap = new HashMap<>();
    @Getter
    private final Map<String, Arena> arenas = new HashMap<>();

    public ArenaManager(PowerJuicePlugin plugin) {
        this.plugin = plugin;
        arenasFolder = new File(plugin.getDataFolder(), "arenas");
        if (!arenasFolder.exists()) {
            arenasFolder.mkdirs();
        }
        initialize();
    }

    private void initialize() {
        File[] files = arenasFolder.listFiles((dir, name) -> name.endsWith(".yml"));
        for (File file : files) {
            String name = file.getName();
            ArenaProperties props = getArenaProperties(name.substring(0, name.length() - 4));
            addArena(new Arena(this, props));
            plugin.getLog().fine("Loaded arena '" + name + "'.");
        }
    }
    
    public List<Arena> getPlayableArenas() {
        List<Arena> playable = new ArrayList<>(this.arenas.values());
        for(int i = 0; i < playable.size(); i++) {
            Arena arena = playable.get(i);
            if(!arena.getProperties().isPlayable()) {
                playable.remove(i);
            }
        }
        return playable;
    }

    /**
     * Gets a {@link File} by arenaName. This file is not guaranteed to actually exist. This method merely constructs
     * a {@link File} object.
     *
     * @param arenaName arena name to get file from
     * @return a {@link File} that belongs to the {@code arenaName}
     */
    private File getArenaFile(@NotNull String arenaName) {
        return new File(arenasFolder, arenaName + ".yml");
    }

    public Arena createArena(@NotNull String arenaName) {
        plugin.getLog().fine("Creating arena '%s'.", arenaName);
        Arena arena = new Arena(this, getArenaProperties(arenaName));
        addArena(arena);
        return arena;
    }

    /**
     * Gets or loads an {@link ArenaProperties}.
     *
     * @param arenaName arena name to get {@link ArenaProperties} for
     * @return {@link ArenaProperties} belonging to {@code arenaName}
     */
    @NotNull
    public ArenaProperties getArenaProperties(@NotNull String arenaName) {
        plugin.getLog().fine("Getting arena properties for '%s'.", arenaName);
        arenaName = arenaName.toLowerCase();
        ArenaProperties properties = this.arenaPropertiesMap.get(arenaName);
        if (properties != null) {
            return properties;
        }
        properties = loadOrCreateArenaProperties(arenaName);
        arenaPropertiesMap.put(arenaName, properties);
        return properties;
    }

    /**
     * Loads an {@link Arena}'s properties.
     *
     * @param arenaName arena name to get {@link ArenaProperties} for
     * @return {@link ArenaProperties} belonging to {@code arenaName} if it exists, otherwise null
     */
    private ArenaProperties loadOrCreateArenaProperties(@NotNull String arenaName) {
        arenaName = arenaName.toLowerCase();
        plugin.getLog().fine("Loading arena properties for '%s'.", arenaName);
        File file = getArenaFile(arenaName);
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            YamlConfiguration config = BukkitConfiguration.loadYamlConfig(file);
            ArenaProperties defaults = new ArenaProperties(arenaName);
            ArenaProperties properties = config.getToObject("settings", defaults);
            if (properties == null) {
                plugin.getLog().fine("Creating defaults arena properties for '%s'.", arenaName);
                properties = defaults;
                saveArenaProperties(properties, config, file);
            }
            return properties;
        } catch (IOException | SendablePluginBaseException e) {
            plugin.getLog().severe("Error occurred while getting arena properties for " + arenaName + ":");
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Saves an {@link ArenaProperties}.
     *
     * @param properties {@link ArenaProperties} to save
     */
    protected void saveArenaProperties(@NotNull ArenaProperties properties) {
        plugin.getLog().fine("Saving arena properties '%s'.", properties.getName());
        File file = getArenaFile(properties.getName());
        try {
            YamlConfiguration config = BukkitConfiguration.loadYamlConfig(file);
            saveArenaProperties(properties, config, file);
        } catch (SendablePluginBaseException e) {
            plugin.getLog().severe("Error occurred while writing arena properties to " + file.getName() + ":");
            e.printStackTrace();
        }
    }

    /**
     * Saves an {@link ArenaProperties}.
     *
     * @param properties {@link ArenaProperties} to save
     * @param config     config to save to
     * @param file       file to save {@code config} to
     */
    private void saveArenaProperties(@NotNull ArenaProperties properties, @NotNull YamlConfiguration config,
                                     @NotNull File file) {
        plugin.getLog().fine("Saving arena properties '%s' to %s.", properties.getName(), file.getName());
        try {
            config.set("settings", properties);
            config.save(file);
        } catch (IOException e) {
            plugin.getLog().severe("Error occurred while writing arena properties to " + file.getName() + ":");
            e.printStackTrace();
        }
    }

    /**
     * Checks whether an {@link Arena} exists in this {@link ArenaManager}.
     *
     * @param arena arena to check
     * @return whether this {@link ArenaManager} has the {@code arena}
     * @see #hasArena(String)
     */
    public boolean hasArena(Arena arena) {
        return hasArena(arena.getName());
    }

    /**
     * Checks whether an {@link Arena} by name exists in this {@link ArenaManager}.
     *
     * @param arenaName arena name to check
     * @return whether this {@link ArenaManager} has an arena by the name of {@code arenaName}
     */
    public boolean hasArena(String arenaName) {
        return this.arenas.containsKey(arenaName.toLowerCase());
    }

    /**
     * Gets an {@link Arena} object by name.
     *
     * @param arenaName arena name to get
     * @return the {@link Arena} object if it exists, otherwise null.
     */
    public Arena getArena(@NotNull String arenaName) {
        return this.arenas.get(arenaName.toLowerCase());
    }

    /**
     * Adds an {@link Arena} to this {@link ArenaManager}.
     *
     * @param arena arena to add
     */
    private void addArena(@NotNull Arena arena) {
        plugin.getLog().fine("Adding arena '%s'.", arena.getName());
        this.arenas.put(arena.getName(), arena);
    }

    /**
     * Removes an {@link Arena} from this {@link ArenaManager}.
     *
     * @param arena arena to remove
     * @return true if the arena was removed
     */
    private boolean removeArena(@NotNull Arena arena) {
        plugin.getLog().fine("Removing arena '%s'.", arena.getName());
        return this.arenas.remove(arena.getName()) != null;
    }
}
