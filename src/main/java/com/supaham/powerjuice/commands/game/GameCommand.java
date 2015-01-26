package com.supaham.powerjuice.commands.game;

import com.supaham.powerjuice.PJException;
import com.supaham.powerjuice.PowerJuicePlugin;
import com.supaham.powerjuice.commands.arena.ArenaCommand;
import com.supaham.powerjuice.game.Game;
import com.supaham.powerjuice.game.GameManager;
import com.supaham.powerjuice.game.GameState;
import com.supaham.powerjuice.game.GamerSession;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.supaham.powerjuice.util.Language.Command.Game.HAS_NOT_BEGUN;
import static com.supaham.powerjuice.util.Language.Command.Game.NOT_INITIALIZED;
import static com.supaham.powerjuice.util.Language.NO_GAMER_SESSION;
import static com.supaham.powerjuice.util.Language.PLAYER_NOT_ONLINE;

public abstract class GameCommand extends ArenaCommand {

    public final GameManager gameManager;
    
    protected GameCommand(@NotNull PowerJuicePlugin plugin, @NotNull GameManager gameManager) {
        super(plugin, plugin.getArenaManager());
        this.gameManager = gameManager;
    }

    @NotNull
    public Game getInitGame() throws PJException {
        GameState state = gameManager.getState();
        if(state != GameState.STARTING) {
            throw new PJException(NOT_INITIALIZED.getParsedMessage());
        }
        return gameManager.getCurrentGame();
    }
    
    @NotNull
    public Game getStartedGame() throws PJException {
        GameState state = gameManager.getState();
        if (state != GameState.STARTING && state != GameState.STARTED) {
            throw new PJException(NOT_INITIALIZED.getParsedMessage());
        }
        if (state != GameState.STARTED) {
            throw new PJException(HAS_NOT_BEGUN.getParsedMessage());
        }
        return gameManager.getCurrentGame();
    }
    
    @NotNull
    public GamerSession getGamerSession(@Nullable String name) throws PJException{
        Game game = getStartedGame();
        Player player = Bukkit.getPlayer(name);
        if(player == null) {
            throw new PJException(PLAYER_NOT_ONLINE.getParsedMessage(name));
        }
        GamerSession session = game.getSession(player);
        if(session == null) {
            throw new PJException(NO_GAMER_SESSION.getParsedMessage(name));
        }
        return session;
    }
}
