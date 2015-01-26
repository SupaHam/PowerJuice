package com.supaham.powerjuice.commands.arena;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.selections.CuboidSelection;
import com.sk89q.worldedit.regions.Region;
import com.supaham.powerjuice.PJException;
import com.supaham.powerjuice.PowerJuicePlugin;
import com.supaham.powerjuice.arena.Arena;
import com.supaham.powerjuice.players.PJPlayer;
import com.supaham.powerjuice.worldedit.annotations.Selection;
import org.jetbrains.annotations.NotNull;
import pluginbase.minecraft.location.Locations;

import static com.supaham.powerjuice.util.Language.Command.Arena.ALREADY_EXISTS;
import static com.supaham.powerjuice.util.Language.Command.Arena.CREATE_SUCCESS;
import static com.supaham.powerjuice.util.Language.Command.Arena.REDEFINE_SUCCESS;

/**
 * Arena commands.
 */
public class ArenaCommands extends ArenaCommand {

    public ArenaCommands(@NotNull PowerJuicePlugin plugin) {
        super(plugin, plugin.getArenaManager());
    }

    @Command(
            aliases = {"create"},
            desc = "Creates an Arena.",
            usage = "<name>",
            help = "Creates an Arena with the boundaries of command sender's WorldEdit region and the name provided " +
                   "as the first argument.",
            min = 1,
            max = 1
    )
    @CommandPermissions("pj.arena.create")
    public void create(PJPlayer sender, @Selection Region region, String arenaName) throws PJException {
        arenaName = arenaName.toLowerCase();
        isValidName(arenaName);

        if (exists(arenaName)) {
            throw new PJException(ALREADY_EXISTS.getParsedMessage(arenaName));
        }

        Arena arena = plugin.getArenaManager().createArena(arenaName);
        Vector min = region.getMinimumPoint();
        Vector max = region.getMaximumPoint();
        arena.setBoundaries(Locations.getCoordinates(min.getX(), min.getY(), min.getZ()),
                            Locations.getCoordinates(max.getX(), max.getY(), max.getZ()));
        arena.save();
        CREATE_SUCCESS.send(sender.getPlayer(), arena.getName());
    }

    @Command(
            aliases = {"redefine", "selection", "sel", "re"},
            desc = "Redefines an Arena's boundaries.",
            usage = "<arena>",
            help = "Redefines an Arena's boundaries. Only supports cuboid selections",
            min = 1,
            max = 1
    )
    @CommandPermissions("pj.arena.redefine")
    public void redefine(PJPlayer sender, @Selection(CuboidSelection.class) Region region, String arenaName)
            throws PJException {
        arenaName = arenaName.toLowerCase();
        Arena arena = getArena(arenaName);
        Vector min = region.getMinimumPoint();
        Vector max = region.getMaximumPoint();
        arena.setBoundaries(Locations.getCoordinates(min.getX(), min.getY(), min.getZ()),
                            Locations.getCoordinates(max.getX(), max.getY(), max.getZ()));
        arena.save();
        REDEFINE_SUCCESS.send(sender.getPlayer(), arena.getName());
    }
}
