package com.supaham.powerjuice.commands.lobby;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.selections.CuboidSelection;
import com.sk89q.worldedit.regions.Region;
import com.supaham.powerjuice.PJException;
import com.supaham.powerjuice.PowerJuicePlugin;
import com.supaham.powerjuice.lobby.Lobby;
import com.supaham.powerjuice.players.PJPlayer;
import com.supaham.powerjuice.worldedit.annotations.Selection;
import org.jetbrains.annotations.NotNull;
import pluginbase.minecraft.location.Locations;

import static com.supaham.powerjuice.util.Language.Command.Lobby.CREATE_SUCCESS;
import static com.supaham.powerjuice.util.Language.Command.Lobby.REDEFINE_SUCCESS;

/**
 * Lobby commands.
 */
public class LobbyCommands extends LobbyCommand {

    public LobbyCommands(@NotNull PowerJuicePlugin plugin) {
        super(plugin, plugin.getLobbyManager());
    }

    @Command(
            aliases = {"create"},
            desc = "Creates the Lobby.",
            help = "Creates the Lobby with the boundaries of command sender's WorldEdit region.\n" +
                   "Only supports cuboid selections.",
            max = 0
    )
    @CommandPermissions("pj.lobby.create")
    public void create(PJPlayer sender, @Selection Region region) throws PJException {
        Lobby lobby = plugin.getLobbyManager().createLobby();
        Vector min = region.getMinimumPoint();
        Vector max = region.getMaximumPoint();
        lobby.setBoundaries(Locations.getCoordinates(min.getX(), min.getY(), min.getZ()),
                            Locations.getCoordinates(max.getX(), max.getY(), max.getZ()));
        lobby.save();
        CREATE_SUCCESS.send(sender.getPlayer());
    }

    @Command(
            aliases = {"redefine", "selection", "sel", "re"},
            desc = "Redefines the Lobby's boundaries.",
            help = "Redefines the Lobby's boundaries.\n" +
                   "Only supports cuboid selections.",
            max = 0
    )
    @CommandPermissions("pj.lobby.redefine")
    public void redefine(PJPlayer sender, @Selection(CuboidSelection.class) Region region) throws PJException {
        Lobby lobby = getLobby();
        Vector min = region.getMinimumPoint();
        Vector max = region.getMaximumPoint();
        lobby.setBoundaries(Locations.getCoordinates(min.getX(), min.getY(), min.getZ()),
                            Locations.getCoordinates(max.getX(), max.getY(), max.getZ()));
        lobby.save();
        REDEFINE_SUCCESS.send(sender.getPlayer());
    }
}
