package com.supaham.powerjuice.commands.arena;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import com.supaham.powerjuice.PJException;
import com.supaham.powerjuice.PowerJuicePlugin;
import com.supaham.powerjuice.arena.Arena;
import com.supaham.powerjuice.players.PJPlayer;
import org.jetbrains.annotations.NotNull;

import static com.supaham.powerjuice.util.Language.Command.Arena.SET_SUCCESS;

/**
 * Arena set commands.
 */
public class ArenaSetCommands extends ArenaCommand {

    public ArenaSetCommands(@NotNull PowerJuicePlugin plugin) {
        super(plugin, plugin.getArenaManager());
    }

    @Command(
            aliases = {"display-name", "name"},
            desc = "Sets an Arena's display name.",
            usage = "<arena> <name...>",
            help = "Sets an Arena's display name. Supports chat colors, color code is '&'.",
            min = 2
    )
    @CommandPermissions("pj.arena.set.display-name")
    public void displayName(PJPlayer sender, Arena arena, String displayName) throws PJException {
//        arenaName = arenaName.toLowerCase();
//        Arena arena = getArena(arenaName);
        arena.setDisplayName(displayName);
        arena.save();
        SET_SUCCESS.send(sender.getPlayer(), arena.getName(), "display name", arena.getDisplayName());
    }

    @Command(
            aliases = {"description", "desc"},
            desc = "Sets an Arena's description.",
            usage = "<arena> <description...>",
            help = "Sets an Arena's description. Supports chat colors, color code is '&'.",
            min = 2
    )
    @CommandPermissions("pj.arena.set.description")
    public void description(PJPlayer sender, String arenaName, String description) throws PJException {
        arenaName = arenaName.toLowerCase();
        Arena arena = getArena(arenaName);
        arena.setDescription(description);
        arena.save();
        SET_SUCCESS.send(sender.getPlayer(), arena.getName(), "description", description);
    }

    @Command(
            aliases = {"authors"},
            desc = "Sets an Arena's authors.",
            usage = "<arena> <authors...>",
            help = "Sets an Arena's authors. Supports chat colors, color code is '&'.",
            min = 2
    )
    @CommandPermissions("pj.arena.set.authors")
    public void authors(PJPlayer sender, String arenaName, String authors) throws PJException {
        arenaName = arenaName.toLowerCase();
        Arena arena = getArena(arenaName);
        arena.setAuthors(authors);
        arena.save();
        SET_SUCCESS.send(sender.getPlayer(), arena.getName(), "authors", authors);
    }
}
