package com.supaham.powerjuice.lobby;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import com.supaham.powerjuice.PJException;
import com.supaham.powerjuice.PowerJuicePlugin;
import com.supaham.powerjuice.game.GameManager;
import com.supaham.powerjuice.game.GameState;
import com.supaham.powerjuice.players.PJPlayer;
import com.supaham.powerjuice.players.PlayerManager;
import com.supaham.powerjuice.util.CollectionUtil;
import com.supaham.powerjuice.util.LocationChecker;
import com.supaham.powerjuice.util.LocationUtil;
import lombok.AccessLevel;
import lombok.Getter;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import pluginbase.bukkit.config.BukkitConfiguration;
import pluginbase.bukkit.config.YamlConfiguration;
import pluginbase.messages.messaging.SendablePluginBaseException;

/**
 * Represents a {@link Lobby} manager.
 */
public class LobbyManager {

    @Getter(AccessLevel.PROTECTED)
    protected final PowerJuicePlugin plugin;
    private File lobbyFolder;
    private LobbyProperties lobbyProperties;
    @Getter
    private Lobby lobby;
    private LobbyScoreboard scoreboard;
    @Getter
    private LocationChecker locationChecker;
    private LobbyTick lobbyTick;
    private LobbyListener lobbyListener;
    @Getter
    private LobbyCountdown lobbyCountdown;
    @Getter
    private Map<UUID, LobbySession> sessions = new HashMap<>();

    @Getter
    private boolean active;

    public LobbyManager(PowerJuicePlugin plugin) {
        this.plugin = plugin;
        lobbyFolder = plugin.getDataFolder();
        if (!lobbyFolder.exists()) {
            lobbyFolder.mkdirs();
        }
        initialize();
    }

    public Lobby createLobby() {
        plugin.getLog().fine("Creating Lobby...");
        Lobby lobby = new Lobby(this, getLobbyProperties());
        return this.lobby = lobby;
    }

    private void initialize() {
        File lobbyFile = getLobbyFile();
        if (!lobbyFile.exists()) {
            plugin.getLog().warning("No lobby defined!");
            return;
        }
        LobbyProperties props = getLobbyProperties();
        this.lobby = new Lobby(this, props);
        plugin.getLog().fine("Loaded Lobby.");
    }

    /**
     * Gets the Lobby's {@link File}. This file is not guaranteed to actually exist. This method merely constructs
     * a {@link File} object.
     *
     * @return a {@link File} that belongs to the Lobby
     */
    private File getLobbyFile() {
        return new File(lobbyFolder, "lobby.yml");
    }

    /**
     * Gets or loads an {@link LobbyProperties}.
     *
     * @return {@link LobbyProperties}
     */
    @NotNull
    public LobbyProperties getLobbyProperties() {
        plugin.getLog().fine("Getting Lobby properties...");
        if (this.lobbyProperties != null) {
            return this.lobbyProperties;
        }
        this.lobbyProperties = loadOrCreateArenaProperties();
        return this.lobbyProperties;
    }

    /**
     * Loads an {@link Lobby}'s properties.
     *
     * @return {@link LobbyProperties} if it exists, otherwise null
     */
    private LobbyProperties loadOrCreateArenaProperties() {
        plugin.getLog().fine("Loading Lobby...");
        File file = getLobbyFile();
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            YamlConfiguration config = BukkitConfiguration.loadYamlConfig(file);
            LobbyProperties defaults = new LobbyProperties();
            LobbyProperties properties = config.getToObject("settings", defaults);
            if (properties == null) {
                plugin.getLog().fine("Creating defaults Lobby properties...");
                properties = defaults;
                saveLobbyProperties(properties, config, file);
            }
            return properties;
        } catch (IOException | SendablePluginBaseException e) {
            plugin.getLog().severe("Error occurred while getting Lobby properties: ");
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Saves an {@link LobbyProperties}.
     *
     * @param properties {@link LobbyProperties} to save
     */
    protected void saveLobbyProperties(@NotNull LobbyProperties properties) {
        plugin.getLog().fine("Saving Lobby properties...");
        File file = getLobbyFile();
        try {
            YamlConfiguration config = BukkitConfiguration.loadYamlConfig(file);
            saveLobbyProperties(properties, config, file);
        } catch (SendablePluginBaseException e) {
            plugin.getLog().severe("Error occurred while writing Lobby properties to " + file.getName() + ":");
            e.printStackTrace();
        }
    }

    /**
     * Saves an {@link LobbyProperties}.
     *
     * @param properties {@link LobbyProperties} to save
     * @param config     config to save to
     * @param file       file to save {@code config} to
     */
    private void saveLobbyProperties(@NotNull LobbyProperties properties, @NotNull YamlConfiguration config,
                                     @NotNull File file) {
        plugin.getLog().fine("Saving Lobby properties to %s...", file.getName());
        try {
            config.options().comments(true);
            config.set("settings", properties);
            config.save(file);
        } catch (IOException e) {
            plugin.getLog().severe("Error occurred while writing Lobby properties to " + file.getName() + ":");
            e.printStackTrace();
        }
    }

    public void activate() {
        plugin.getLog().fine("Activating Lobby...");
        Validate.isTrue(!active, "LobbyManager is already active.");

        this.scoreboard = new LobbyScoreboard(this);
        
        this.locationChecker = new LocationChecker(getLobby().getMinimumPoint(), getLobby().getMaximumPoint()) {
            @Override
            public List<Player> getPlayers() {
                return PlayerManager.unignoredPlayers(LobbyManager.this.sessions.values().stream().
                        map(LobbySession::getPJPlayer).collect(Collectors.toCollection(LinkedList::new)));
            }
        };
        this.locationChecker.start();
        
        this.lobbyTick = new LobbyTick(this);
        this.lobbyTick.start();
        plugin.regEvents(this.lobbyListener = new LobbyListener(this));
        this.lobbyCountdown = new LobbyCountdown(this);
        active = true;
    }

    public void deactivate() {
        plugin.getLog().fine("Deactivating Lobby...");
        if (sessions.size() > 0) { // There are players in the lobby, lets not ruin their experience
            return;
        }

        if (this.locationChecker != null) {
            this.locationChecker.cancel();
            this.locationChecker = null;
        }

        this.scoreboard.clear();
        this.scoreboard = null;
        HandlerList.unregisterAll(this.lobbyListener);
        this.lobbyListener = null;
        
        this.lobbyCountdown = null;
        
        this.lobbyTick.cancel();
        this.lobbyTick = null;
        
        active = false;
    }

    /**
     * Gets a random spawn {@link Location}.
     *
     * @return random spawn
     */
    public Location getRandomSpawn() {
        if (this.lobby == null) {
            return null;
        }
        return LocationUtil.coordsToLocation(
                CollectionUtil.getRandomElement(this.lobby.getSpawns()), Bukkit.getWorlds().get(0));
    }
    
    public boolean isInLobby(@NotNull PJPlayer pjPlayer) {
        LobbySession session = this.sessions.get(pjPlayer.getPlayer().getUniqueId());
        return session != null && session.isActive();
    }

    /**
     * Adds a {@link PJPlayer} to this lobby.
     *
     * @param pjPlayer {@link PJPlayer} to add
     * @return whether the player was added to the lobby or not. The player wouldn't be added if they were already in
     * the lobby.
     * @throws PJException thrown if the Lobby hasn't been created
     */
    public LobbySession addPlayer(@NotNull PJPlayer pjPlayer) throws PJException {
        plugin.getLog().fine("Adding " + pjPlayer.getName() + " to the lobby.");

        if (!isActive()) activate();

        GameManager gameManager = plugin.getGameManager();
        if (gameManager.getState() != GameState.STARTING && gameManager.getState() != GameState.STARTED) {
            if (this.plugin.getServer().getOnlinePlayers().size() >= 2) {
                lobbyCountdown.start();
            }
        }
        
        LobbySession session = this.sessions.get(pjPlayer.getPlayer().getUniqueId());
        if (session != null) {
            session.setup();
            return session;
        }

        if (lobby == null || !lobby.getProperties().isPlayable()) {
            throw new PJException("The lobby is not set up.");
        }

        UUID uuid = pjPlayer.getPlayer().getUniqueId();
        session = LobbySession.createSession(this, pjPlayer);
        this.sessions.put(uuid, session);
        session.setup();
        return session;
    }

    public void destroySession(@NotNull LobbySession session) {
        plugin.getLog().fine("Destroying " + session);
        Player player = session.getBukkitPlayer();
        this.locationChecker.getOutOfBounders().remove(player);
        this.scoreboard.removePlayer(player);
        session.reset();
    }

    /**
     * Removes a {@link PJPlayer} from this lobby.
     *
     * @param pjPlayer PJPlayer to remove.
     * @return whether the player was removed from the lobby.
     */
    public boolean removePlayer(@NotNull PJPlayer pjPlayer) {
        plugin.getLog().fine("Removing " + pjPlayer.getName() + " from the lobby.");
        LobbySession removed = this.sessions.remove(pjPlayer.getPlayer().getUniqueId());
        if (removed != null) {
            plugin.getLog().fine("Removed " + pjPlayer.getName() + " from the lobby.");
            destroySession(removed);
            if (isActive() && this.sessions.size() == 0) { // No one is in the lobby, lets deactivate it.
                deactivate();
            }
        }
        return removed != null;
    }
}
