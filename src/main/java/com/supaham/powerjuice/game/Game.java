package com.supaham.powerjuice.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.stream.Collectors;

import com.supaham.powerjuice.PJException;
import com.supaham.powerjuice.PowerJuicePlugin;
import com.supaham.powerjuice.arena.Arena;
import com.supaham.powerjuice.events.game.GameStopEvent;
import com.supaham.powerjuice.events.game.GameStopEvent.Reason;
import com.supaham.powerjuice.events.game.GamerJoinEvent;
import com.supaham.powerjuice.game.GamerSessionComparators.GamerSessionComparator;
import com.supaham.powerjuice.players.PJPlayer;
import com.supaham.powerjuice.powerup.PowerupManager;
import com.supaham.powerjuice.util.CollectionUtil;
import com.supaham.powerjuice.util.LocationChecker;
import com.supaham.powerjuice.util.LocationUtil;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.supaham.powerjuice.game.GamerSessionComparators.GamerSessionComparator.MOST_KILLS;
import static com.supaham.powerjuice.game.GamerSessionComparators.GamerSessionComparator.MOST_POINTS;

/**
 * Represents a Gaming session based on an arena.
 */
@Getter
public class Game {

    private final GameManager manager;
    private final PowerJuicePlugin plugin;
    private final Arena arena;
    @Getter
    private final World world;

    private GameProperties properties;
    private GameListener gameListener;
    private GameTick tickTask;
    private LocationChecker locationChecker;
    private GameScoreboard scoreboard;
    private PowerupManager powerupManager;
    
    private GameBossBar bossBar;
    private GameStorm storm;

    private final Map<UUID, GamerSession> sessions = new HashMap<>();
    
    private List<GamerSession> winners = new ArrayList<>();

    public Game(@NotNull GameManager manager, @NotNull Arena arena) {
        this.manager = manager;
        this.plugin = manager.getPlugin();
        this.arena = arena;
        this.world = Bukkit.getWorlds().get(0);
        this.properties = new GameProperties(this);
    }

    public void init() {
        plugin.getLog().fine("Initializing " + this + " in arena " + arena.getName());

        properties.init();

        PluginManager pm = plugin.getServer().getPluginManager();
        pm.registerEvents(this.gameListener = new GameListener(this), plugin);

        this.tickTask = new GameTick(this);
        this.locationChecker = new LocationChecker(getArena().getMinimumPoint(), getArena().getMaximumPoint()) {
            @Override
            public List<Player> getPlayers() {
                return sessions.values().stream().filter(GamerSession::isPlaying)
                        .map(GamerSession::getBukkitPlayer).collect(Collectors.toList());
            }
        };
        this.scoreboard = new GameScoreboard(this);
        this.powerupManager = new PowerupManager(plugin);
        this.powerupManager.load();

        this.bossBar = new GameBossBar(this);
        this.storm = new GameStorm(this);
        this.storm.init();
        // This has to be initialized last
        this.tickTask.init();
    }

    public void start() {
        plugin.getLog().fine("Starting " + this + "...");
        this.properties.start();
        for (PJPlayer pjPlayer : plugin.getPlayerManager().getPJPlayers().values()) {
            addSession(pjPlayer).setup();
        }

        this.tickTask.start();
        this.locationChecker.runTaskTimer(plugin, 0, 20);

        // Clean up any existing physical powerups.
        this.powerupManager.clearSpawnedPowerups(getProperties().getPowerupLocations());
        
        // Delayed task to give chunks time to load
        new BukkitRunnable() {
            @Override
            public void run() {
                powerupManager.spawnRandomPowerup(getProperties().getPowerupLocations(), null);
            }
        }.runTaskLater(this.plugin, 20L);
        this.world.setStorm(false);
    }

    protected void stop(@NotNull Reason reason) {
        plugin.getLog().fine("Stopping game...");

        boolean ending = manager.getState().equals(GameState.ENDED);

        HandlerList.unregisterAll(gameListener);

        if (ending) {
            if (winners.isEmpty()) {
                findWinners(MOST_POINTS);
            }

            Bukkit.getPluginManager().callEvent(new GameStopEvent(this, reason));

            Iterator<Entry<UUID, GamerSession>> it = this.sessions.entrySet().iterator();

            while (it.hasNext()) {
                GamerSession session = it.next().getValue();
                destroySession(session);
                it.remove();
            }

            String str = "";
            if (!this.winners.isEmpty()) {
                for (GamerSession winner : this.winners) {
                    str += winner.getName() + ", ";
                }
                str = str.substring(0, str.length() - 2);
                Bukkit.broadcastMessage(ChatColor.BLUE + str + ChatColor.YELLOW + " won the game.");
            } else {
                Bukkit.broadcastMessage(ChatColor.YELLOW + "No one won the game.");
            }
            this.locationChecker.cancel();
            this.tickTask.cancel();
            this.scoreboard.clear();
            this.powerupManager.clearSpawnedPowerups(getProperties().getPowerupLocations());
        }
        
        this.locationChecker = null;
        this.tickTask = null;
        this.scoreboard = null;
        this.powerupManager = null;
        this.bossBar = null;
        this.storm = null;
        
        properties.stop();
    }

    private void findWinners(@NotNull GamerSessionComparator comparator) {
        List<GamerSession> sort = GamerSessionComparators.sort(this, comparator);
        this.winners = comparator.equals(MOST_POINTS) ? MOST_KILLS.getRelevantSessions(sort, false) :
                       comparator.getRelevantSessions(sort);
    }

    public void broadcastMessage(@NotNull String message) {
        for (GamerSession session : getGamerSessions()) {
            if (!session.isOnline()) continue;
            session.getBukkitPlayer().sendMessage(message);
        }
    }

    /**
     * Gets the next spawn {@link Location} a player should spawn at.
     *
     * @return next spawnpoint
     */
    public Location getNextSpawn() {
        return getRandomSpawn();
    }

    /**
     * Gets a random spawn {@link Location}.
     *
     * @return random spawn
     */
    public Location getRandomSpawn() {
        return LocationUtil.coordsToLocation(CollectionUtil.getRandomElement(arena.getSpawns()), this.world);
    }
    
    public long getEndTime() {
        return properties.getEndTime();
    }

    public void setEndTime(long millis) {
        properties.setEndTime(millis);
    }

    /**
     * Checks whether a {@link PJPlayer} has a {@link GamerSession} in this {@link Game}.
     *
     * @param pjPlayer {@link PJPlayer} to check
     * @return whether the {@code player} has a {@link GamerSession}
     */
    public boolean hasSession(@NotNull PJPlayer pjPlayer) {
        return hasSession(pjPlayer.getPlayer());
    }

    /**
     * Checks whether a {@link Player} has a {@link GamerSession} in this {@link Game}.
     *
     * @param player {@link Player} to check
     * @return whether the {@code player} has a {@link GamerSession}
     */
    public boolean hasSession(@NotNull Player player) {
        return this.sessions.containsKey(player.getUniqueId());
    }
    
    /**
     * Gets a new {@link List} of {@link GamerSession}s that are currently playing in this {@link Game}.
     *
     * @return new {@link List} of {@link GamerSession}s
     */
    public List<GamerSession> getPlayingSessions() {
        return this.sessions.values().stream().filter(GamerSession::isPlaying).collect(Collectors.toList());
    }

    /**
     * Gets a new {@link List} of this {@link Game}'s {@link GamerSession}s.
     *
     * @return new {@link List} of {@link GamerSession}s
     */
    public List<GamerSession> getGamerSessions() {
        return new ArrayList<>(this.sessions.values());
    }

    /**
     * Gets a {@link GamerSession} belonging to a {@link PJPlayer}.
     *
     * @param pjPlayer {@link PJPlayer} to get {@link Player} from.
     * @return the {@link GamerSession} if it was found
     */
    @Nullable
    public GamerSession getSession(@NotNull PJPlayer pjPlayer) {
        return getSession(pjPlayer.getPlayer(), false);
    }

    /**
     * Gets a {@link GamerSession} belonging to a {@link Player}.
     *
     * @param player player that owns the {@link GamerSession}
     * @return the {@link GamerSession} if it was found
     */
    @Nullable
    public GamerSession getSession(@NotNull Player player) {
        return getSession(player, false);
    }

    /**
     * Gets a {@link GamerSession} belonging to a {@link Player}.
     *
     * @param player      player that owns the {@link GamerSession}
     * @param throwIfNull whether to throw an exception if the {@code player} doesn't have a {@link GamerSession}
     * @return the {@link GamerSession} if it was found
     */
    @Nullable
    public GamerSession getSession(@NotNull Player player, boolean throwIfNull) {
        GamerSession session = this.sessions.get(player.getUniqueId());
        if (throwIfNull && session == null) {
            throw new IllegalArgumentException(player.getUniqueId() + "(" + player.getName() + ") doesn't " +
                                               "have a GamerSession instance.");
        }
        return session;
    }

    @NotNull
    public GamerSession addSession(@NotNull PJPlayer pjPlayer) {
        plugin.getLog().fine("Adding " + GamerSession.class.getSimpleName() + " for " + pjPlayer.getName() + "...");
        plugin.getLobbyManager().removePlayer(pjPlayer);
        UUID uuid = pjPlayer.getPlayer().getUniqueId();
        
        GamerSession session;
        if(hasStarted()) {
            session = this.sessions.get(uuid);
            if (session == null) {
                session = GamerSession.createSession(pjPlayer, this, true);
            }
        } else {
            session = GamerSession.createSession(pjPlayer, this, false);
        }
        
        this.scoreboard.addSession(session);
        Bukkit.getPluginManager().callEvent(new GamerJoinEvent(session));
        this.sessions.put(uuid, session);
        return session;
    }

    public void destroySession(@NotNull GamerSession session) {
        this.scoreboard.removeSession(session);

        if(!session.getPJPlayer().isQuitting()) {
            try {
                plugin.getLobbyManager().addPlayer(session.getPJPlayer());
            } catch (PJException e) {
                session.getBukkitPlayer().kickPlayer(e.getMessage());
                e.printStackTrace();
            }
        }
        session.reset();
    }

    /**
     * Removes a {@link GamerSession}.
     *
     * @param session session to remove
     * @return whether the session was removed
     */
    public boolean removeSession(@NotNull GamerSession session) {
        return removeSession(session.getBukkitPlayer()) != null;
    }

    /**
     * Removes a {@link GamerSession}.
     *
     * @param player player that owns the {@link GamerSession} to remove
     * @return the {@link GamerSession} that was removed, otherwise null if no session was found
     */
    @Nullable
    public GamerSession removeSession(@NotNull Player player) {
        GamerSession session = this.sessions.remove(player.getUniqueId());
        if (session != null) {
            destroySession(session);
        }
        return session;
    }

    public boolean hasStarted() {
        return this.manager.hasStarted();
    }
}
