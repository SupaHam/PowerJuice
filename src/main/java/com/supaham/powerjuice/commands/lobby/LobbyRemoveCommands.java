package com.supaham.powerjuice.commands.lobby;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import com.supaham.powerjuice.PJException;
import com.supaham.powerjuice.PowerJuicePlugin;
import com.supaham.powerjuice.lobby.Lobby;
import com.supaham.powerjuice.players.PJPlayer;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import pluginbase.messages.messaging.Messager;
import pluginbase.minecraft.location.FacingCoordinates;

import static com.supaham.powerjuice.util.Language.Command.Lobby.REMOVE_SPAWN_FAILED;
import static com.supaham.powerjuice.util.Language.Command.Lobby.REMOVE_SPAWN_SUCCESS;

/**
 * Lobby remove commands.
 */
public class LobbyRemoveCommands extends LobbyCommand {

    public LobbyRemoveCommands(@NotNull PowerJuicePlugin plugin) {
        super(plugin, plugin.getLobbyManager());
    }

    @Command(
            aliases = {"spawn"},
            desc = "Removes a spawnpoint from the Lobby.",
            help = "Removes a spawnpoint from the Lobby.",
            min = 0,
            max = 0
    )
    @CommandPermissions("pj.lobby.remove.spawn")
    public void spawn(PJPlayer sender) throws PJException {
        Player player = sender.getPlayer();
        Lobby lobby = checkStandingInLobby(player);
        Location loc = player.getLocation();
        FacingCoordinates match = lobby.fuzzySpawnMatch(loc);
        if (match == null) {
            Messager messager = Messager.requestMessager();
            if(messager != null) {
                throw new PJException(REMOVE_SPAWN_FAILED.getParsedMessage());
            }
            throw new PJException("There isn't a spawnpoint at your location.");
        }
        lobby.removeSpawn(match);
        lobby.save();
        REMOVE_SPAWN_SUCCESS.send(sender.getPlayer());
    }
}
