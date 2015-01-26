package com.supaham.powerjuice.commands.lobby;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import com.sk89q.worldedit.util.command.binding.Switch;
import com.supaham.powerjuice.PJException;
import com.supaham.powerjuice.PowerJuicePlugin;
import com.supaham.powerjuice.lobby.Lobby;
import com.supaham.powerjuice.players.PJPlayer;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import pluginbase.minecraft.location.FacingCoordinates;

import static com.supaham.powerjuice.util.Language.Command.Lobby.ADD_SPAWN_SUCCESS;
import static com.supaham.powerjuice.util.Language.Command.Lobby.ADD_SPAWN_SUCCESS_OVERWRITE;

/**
 * Lobby add commands.
 */
public class LobbyAddCommands extends LobbyCommand {

    public LobbyAddCommands(@NotNull PowerJuicePlugin plugin) {
        super(plugin, plugin.getLobbyManager());
    }

    @Command(
            aliases = {"spawn"},
            desc = "Adds a spawnpoint to the Lobby.",
            help = "Adds a spawnpoint to the Lobby.",
            min = 0,
            max = 0,
            flags = "o"
    )
    @CommandPermissions("pj.lobby.add.spawn")
    public void spawn(PJPlayer sender, @Switch('o') boolean override) throws PJException {
        Player player = sender.getPlayer();
        Lobby lobby = checkStandingInLobby(player);
        Location loc = player.getLocation();
        FacingCoordinates match = lobby.fuzzySpawnMatch(loc);
        if (match != null) {
            if (!override) {
                throw new PJException("There already is a spawn at your location, Use -o to override it.");
            }
            lobby.removeSpawn(match);
        }
        lobby.addSpawn(loc);
        lobby.save();
        if (match == null) {
            ADD_SPAWN_SUCCESS.send(sender.getPlayer());
        } else {
            ADD_SPAWN_SUCCESS_OVERWRITE.send(sender.getPlayer());
        }
    }
}
