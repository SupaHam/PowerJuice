package com.supaham.powerjuice.commands.arena;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import com.supaham.powerjuice.PJException;
import com.supaham.powerjuice.PowerJuicePlugin;
import com.supaham.powerjuice.arena.Arena;
import com.supaham.powerjuice.players.PJPlayer;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import pluginbase.minecraft.location.Coordinates;
import pluginbase.minecraft.location.FacingCoordinates;

import static com.supaham.powerjuice.util.Language.Command.Arena.REMOVE_POWERUP_FAILED;
import static com.supaham.powerjuice.util.Language.Command.Arena.REMOVE_POWERUP_SUCCESS;
import static com.supaham.powerjuice.util.Language.Command.Arena.REMOVE_SPAWN_FAILED;
import static com.supaham.powerjuice.util.Language.Command.Arena.REMOVE_SPAWN_SUCCESS;

/**
 * Arena remove commands.
 */
public class ArenaRemoveCommands extends ArenaCommand {

    public ArenaRemoveCommands(@NotNull PowerJuicePlugin plugin) {
        super(plugin, plugin.getArenaManager());
    }

    @Command(
            aliases = {"spawn"},
            desc = "Removes a spawnpoint from the Arena the command sender is standing in.",
            help = "Removes a spawnpoint from the Arena the command sender is standing in.",
            min = 0,
            max = 0
    )
    @CommandPermissions("pj.arena.remove.spawn")
    public void spawn(PJPlayer sender) throws PJException {
        Player player = sender.getPlayer();
        Arena arena = findArenaStandingIn(player);
        Location loc = player.getLocation();
        FacingCoordinates match = arena.fuzzySpawnMatch(loc);
        if (match == null) {
            throw new PJException(REMOVE_SPAWN_FAILED.getParsedMessage(arena.getName()));
        }
        arena.removeSpawn(match);
        arena.save();
        REMOVE_SPAWN_SUCCESS.send(sender.getPlayer(), arena.getName());
    }

    @Command(
            aliases = {"powerup"},
            desc = "Removes a powerup from the Arena the command sender is standing in.",
            help = "Removes a powerup from the Arena the command sender is standing in.",
            max = 0
    )
    @CommandPermissions("pj.arena.remove.spawn")
    public void powerup(PJPlayer sender) throws PJException {
        Player player = sender.getPlayer();
        Arena arena = findArenaStandingIn(player);
        Location loc = player.getLocation();
        Coordinates match = arena.fuzzyPowerupLocationMatch(loc);
        if (match == null) {
            throw new PJException(REMOVE_POWERUP_FAILED.getParsedMessage(arena.getName()));
        }
        arena.removePowerupLocation(match);
        arena.save();
        sender.send(REMOVE_POWERUP_SUCCESS, arena.getName());
    }
}
