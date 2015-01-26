package com.supaham.powerjuice.game;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.supaham.powerjuice.PowerJuicePlugin;
import com.supaham.powerjuice.arena.Arena;
import com.supaham.powerjuice.events.game.GameStopEvent.Reason;
import com.supaham.powerjuice.lobby.LobbyCountdown;
import com.supaham.powerjuice.players.PJPlayer;
import com.supaham.powerjuice.util.CollectionUtil;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a {@link Game} manager.
 */
public class GameManager {
    @Getter
    private final PowerJuicePlugin plugin;

    @Getter
    private Game currentGame;

    @Getter
    private GameState state = GameState.WAITING_FOR_PLAYERS;

    public GameManager(@NotNull PowerJuicePlugin plugin) {
        this.plugin = plugin;
    }

    public Game init() {
        return init(null);
    }

    public Game init(@Nullable Arena arena) {
        if (arena == null) {
            List<Arena> values = plugin.getArenaManager().getPlayableArenas();
            if (values.isEmpty()) {
                throw new IllegalStateException("No playable arenas available.");
            }
            arena = CollectionUtil.getRandomElement(values);
        }
        this.currentGame = new Game(this, arena);
        this.currentGame.init();
        state = GameState.STARTING;
        return this.currentGame;
    }

    public void start() {
        start(false);
    }
    
    public void start(boolean forceStart) {
        if (this.currentGame == null) {
            Game init = init();
            if (init == null) {
                throw new IllegalStateException("Cannot start if the game has not been initialized.");
            }
        } else if (this.state == GameState.STARTED) {
            throw new IllegalStateException("Game has already started.");
        }

        LobbyCountdown countdown = plugin.getLobbyManager().getLobbyCountdown();
        if (countdown.isStarted()) {
            countdown.stop();
        }
        
        if (!forceStart && this.plugin.getServer().getOnlinePlayers().size() < 2) {
            return;
        }
        this.currentGame.start();
        state = GameState.STARTED;
    }

    public void stop(@NotNull Reason reason) {
        if (this.currentGame == null) {
            throw new IllegalStateException("Cannot stop if the game has not been initialized.");
        }
        state = GameState.ENDED;
        this.currentGame.stop(reason);
        this.currentGame = null;

    }

    /**
     * @see Game#getSessions()
     */
    public Map<UUID, GamerSession> getSessions() {
        return currentGame.getSessions();
    }
    
    public boolean isPlaying(@NotNull PJPlayer pjPlayer) {
        GamerSession session = this.currentGame.getSession(pjPlayer);
        return session != null && session.isPlaying();
    }

    /**
     * @see Game#hasSession(PJPlayer)
     */
    public boolean hasSession(@NotNull PJPlayer pjPlayer) {
        return currentGame.hasSession(pjPlayer);
    }

    /**
     * @see Game#hasSession(Player)
     */
    public boolean hasSession(@NotNull Player player) {
        return currentGame.hasSession(player);
    }

    public List<GamerSession> getGamerSessions() {
        return currentGame.getGamerSessions();
    }

    @Nullable
    public GamerSession getSession(@NotNull PJPlayer pjPlayer) {
        return currentGame.getSession(pjPlayer);
    }

    /**
     * @see Game#getSession(Player)
     */
    @Nullable
    public GamerSession getSession(@NotNull Player player) {
        return currentGame.getSession(player);
    }

    /**
     * @see Game#addSession(PJPlayer)
     */
    @Nullable
    public GamerSession addSession(@NotNull PJPlayer pjPlayer) {
        return currentGame.addSession(pjPlayer);
    }

    /**
     * @see Game#removeSession(GamerSession)
     */
    public boolean removeSession(@NotNull GamerSession session) {
        return currentGame.removeSession(session);
    }

    /**
     * @see Game#removeSession(Player)
     */
    @Nullable
    public GamerSession removeSession(@NotNull Player player) {
        return currentGame.removeSession(player);
    }

    public boolean hasStarted() {
        return this.state.equals(GameState.STARTED);
    }
}
