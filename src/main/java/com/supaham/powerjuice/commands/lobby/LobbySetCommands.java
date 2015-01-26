package com.supaham.powerjuice.commands.lobby;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import com.supaham.powerjuice.PJException;
import com.supaham.powerjuice.PowerJuicePlugin;
import com.supaham.powerjuice.lobby.Lobby;
import com.supaham.powerjuice.players.PJPlayer;
import org.jetbrains.annotations.NotNull;

import static com.supaham.powerjuice.util.Language.Command.Lobby.SET_SUCCESS;

/**
 * Lobby set commands.
 */
public class LobbySetCommands extends LobbyCommand {

    public LobbySetCommands(@NotNull PowerJuicePlugin plugin) {
        super(plugin, plugin.getLobbyManager());
    }

    @Command(
            aliases = {"authors"},
            desc = "Sets the Lobby's authors.",
            usage = "<authors...>",
            help = "Sets the Lobby's authors. Supports chat colors, color code is '&'.",
            min = 1
    )
    @CommandPermissions("pj.lobby.set.authors")
    public void authors(PJPlayer sender, String authors) throws PJException {
        Lobby lobby = getLobby();
        lobby.setAuthors(authors);
        lobby.save();
        SET_SUCCESS.send(sender.getPlayer(), "authors", authors);
    }
}
