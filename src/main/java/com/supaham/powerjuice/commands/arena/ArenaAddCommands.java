package com.supaham.powerjuice.commands.arena;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import com.sk89q.worldedit.util.command.binding.Switch;
import com.supaham.powerjuice.PJException;
import com.supaham.powerjuice.PowerJuicePlugin;
import com.supaham.powerjuice.arena.Arena;
import com.supaham.powerjuice.players.PJPlayer;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import pluginbase.minecraft.location.Coordinates;
import pluginbase.minecraft.location.FacingCoordinates;

import static com.supaham.powerjuice.util.Language.Command.Arena.ADD_POWERUP_SUCCESS;
import static com.supaham.powerjuice.util.Language.Command.Arena.ADD_POWERUP_SUCCESS_OVERWRITE;
import static com.supaham.powerjuice.util.Language.Command.Arena.ADD_SPAWN_SUCCESS;
import static com.supaham.powerjuice.util.Language.Command.Arena.ADD_SPAWN_SUCCESS_OVERWRITE;

/**
 * Arena add commands.
 */
public class ArenaAddCommands extends ArenaCommand {

    public ArenaAddCommands(@NotNull PowerJuicePlugin plugin) {
        super(plugin, plugin.getArenaManager());
    }

    @Command(
            aliases = {"spawn"},
            desc = "Adds a spawnpoint to the Arena the command sender is standing in.",
            help = "Adds a spawnpoint to the Arena the command sender is standing in.",
            min = 0,
            max = 0,
            flags = "o"
    )
    @CommandPermissions("pj.arena.add.spawn")
    public void spawn(PJPlayer sender, @Switch('o') boolean override) throws PJException {
        Player player = sender.getPlayer();
        Arena arena = findArenaStandingIn(player);
        Location loc = player.getLocation();
        FacingCoordinates match = arena.fuzzySpawnMatch(loc);
        if (match != null) {
            if (!override) {
                throw new PJException("There already is a spawn at your location, Use -o to override it.");
            }
            arena.removeSpawn(match);
        }
        arena.addSpawn(loc);
        arena.save();
        sender.send(match != null ? ADD_SPAWN_SUCCESS_OVERWRITE : ADD_SPAWN_SUCCESS, arena.getName());
    }

    @Command(
            aliases = {"powerup"},
            desc = "Adds a powerup to the Arena the command sender is standing in.",
            help = "Adds a powerup to the Arena the command sender is standing in.",
            min = 0,
            max = 0,
            flags = "o"
    )
    @CommandPermissions("pj.arena.add.powerup")
    public void powerup(PJPlayer sender, @Switch('o') boolean override) throws PJException {
        Player player = sender.getPlayer();
        Arena arena = findArenaStandingIn(player);
        Location loc = player.getLocation();
        Coordinates match = arena.fuzzyPowerupLocationMatch(loc);
        if (match != null) {
            if (!override) {
                throw new PJException("There already is a spawn at your location, Use -o to override it.");
            }
            arena.removePowerupLocation(match);
        }
        arena.addPowerupLocation(loc);
        arena.save();
        sender.send(match != null ? ADD_POWERUP_SUCCESS_OVERWRITE : ADD_POWERUP_SUCCESS, arena.getName());
    }
}
