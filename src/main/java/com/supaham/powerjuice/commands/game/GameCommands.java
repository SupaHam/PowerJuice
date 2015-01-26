package com.supaham.powerjuice.commands.game;

import java.util.concurrent.TimeUnit;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import com.sk89q.worldedit.util.command.parametric.Optional;
import com.supaham.powerjuice.PJException;
import com.supaham.powerjuice.PowerJuicePlugin;
import com.supaham.powerjuice.events.game.GameStopEvent.Reason;
import com.supaham.powerjuice.game.Game;
import com.supaham.powerjuice.game.GameState;
import com.supaham.powerjuice.players.PJPlayer;
import com.supaham.powerjuice.util.Language;
import org.jetbrains.annotations.NotNull;

import static com.supaham.powerjuice.util.Language.Command.Game.ALREADY_BEGUN;
import static com.supaham.powerjuice.util.Language.Command.Game.ALREADY_INITIALIZED;
import static com.supaham.powerjuice.util.Language.Command.Game.INIT_SUCCESS;
import static com.supaham.powerjuice.util.Language.Command.Game.NOT_INITIALIZED;
import static com.supaham.powerjuice.util.Language.Command.Game.START_SUCCESS;
import static com.supaham.powerjuice.util.Language.Command.Game.STOP_SUCCESS;
import static com.supaham.powerjuice.util.Language.Command.Game.TIME_INCREASE_SUCCESS;
import static com.supaham.powerjuice.util.Language.Command.Game.TIME_NO_ZERO;
import static com.supaham.powerjuice.util.Language.Command.Game.TIME_REDUCE_SUCCESS;
import static com.supaham.powerjuice.util.Language.Command.Game.TIME_SET_SUCCESS;


/**
 * Game commands.
 */
public class GameCommands extends GameCommand {

    public GameCommands(@NotNull PowerJuicePlugin plugin) {
        super(plugin, plugin.getGameManager());
    }

    @Command(
            aliases = {"init"},
            desc = "Initializes a Game.",
            usage = "[arena]",
            help = "Initializes a Game. \nIf no arguments are passed, it picks a random arena, " +
                   "otherwise it will pick the arena as the first argument.",
            min = 0,
            max = 1
    )
    @CommandPermissions("pj.game.init")
    public void init(PJPlayer sender, @Optional String arenaName) throws PJException {
        GameState state = gameManager.getState();
        if (state == GameState.STARTING) {
            throw new PJException(ALREADY_INITIALIZED.getParsedMessage());
        } else if (state == GameState.STARTED) {
            throw new PJException(ALREADY_BEGUN.getParsedMessage());
        }
        try {
            Game game = gameManager.init(arenaName == null ? null : getArena(arenaName));
            INIT_SUCCESS.send(sender.getPlayer(), game.getArena().getName());
        } catch (IllegalStateException e) {
            throw new PJException(e.getMessage());
        }
    }

    @Command(
            aliases = {"start"},
            desc = "Starts the Game.",
            help = "Starts the Game.",
            max = 0
    )
    @CommandPermissions("pj.game.start")
    public void start(PJPlayer sender) throws PJException {
        GameState state = gameManager.getState();
        if (state != GameState.STARTING && state != GameState.STARTED) {
            throw new PJException(NOT_INITIALIZED.getParsedMessage());
        }
        if (state == GameState.STARTED) {
            throw new PJException(ALREADY_BEGUN.getParsedMessage());
        }
        gameManager.start(true);
        START_SUCCESS.send(sender.getPlayer());
    }

    @Command(
            aliases = {"stop"},
            desc = "Stops the Game.",
            usage = "[winner]",
            help = "Stops the Game.",
            min = 0,
            max = 1
    )
    @CommandPermissions("pj.game.stop")
    public void stop(PJPlayer sender, @Optional String winnerName) throws PJException {
        GameState state = gameManager.getState();
        if (state != GameState.STARTING && state != GameState.STARTED) {
            throw new PJException(NOT_INITIALIZED.getParsedMessage());
        }
        if (winnerName != null) {
            PJPlayer pjPlayer = getPJPlayer(winnerName);
            gameManager.getCurrentGame().getWinners().add(gameManager.getSession(pjPlayer));
        }
        gameManager.stop(Reason.COMMAND);
        STOP_SUCCESS.send(sender.getPlayer());
    }

    @Command(
            aliases = {"time"},
            desc = "Modifies the remaining time in the game.",
            usage = "<time>",
            help = "Modifies the remaining time in the game. If the string begins with '~' it will modify the current" +
                   " remaining time with the following statement. E.g.: ~-20 will reduce the current time by 20 " +
                   "seconds. ~20 will increase the current time by 20 seconds.",
            min = 1,
            max = 1
    )
    @CommandPermissions("pj.game.time")
    public void time(PJPlayer sender, String time) throws PJException {
        Game game = getStartedGame();
        boolean modifying = time.charAt(0) == '~';
        if (modifying) {
            int rem = Integer.parseInt(time.substring(1));
            if (rem == 0) {
                throw new PJException(TIME_NO_ZERO.getParsedMessage());
            }
            game.setEndTime(game.getEndTime() + TimeUnit.SECONDS.toMillis(rem));
            
            if(rem > 0) {
                TIME_INCREASE_SUCCESS.send(sender.getPlayer(), rem);
                game.broadcastMessage(Language.Game.TIME_INCREASE_SUCCESS.getParsedMessage(rem));
            } else {
                TIME_REDUCE_SUCCESS.send(sender.getPlayer(), rem);
                game.broadcastMessage(Language.Game.TIME_REDUCE_SUCCESS.getParsedMessage(rem));
            }
        } else {
            int rem = Integer.parseInt(time);
            if(rem <= 0) {
                throw new PJException("time must be larger than 0");
            }
            game.setEndTime(System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(rem));
            game.broadcastMessage(Language.Game.TIME_SET_SUCCESS.getParsedMessage(rem));
            TIME_SET_SUCCESS.send(sender.getPlayer(), rem);
        }
    }
}
