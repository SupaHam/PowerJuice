package com.supaham.powerjuice.commands;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import com.sk89q.worldedit.util.command.parametric.Optional;
import com.supaham.powerjuice.PJException;
import com.supaham.powerjuice.PowerJuicePlugin;
import com.supaham.powerjuice.players.PJPlayer;
import org.jetbrains.annotations.NotNull;

import static com.supaham.powerjuice.util.Language.Command.General.IGNORE_FALSE;
import static com.supaham.powerjuice.util.Language.Command.General.IGNORE_TRUE;

public class GeneralCommands extends PJCommand {

    protected GeneralCommands(@NotNull PowerJuicePlugin plugin) {
        super(plugin);
    }

    @Command(
            aliases = {"ignore"},
            desc = "Ignores a player from the game.",
            help = "Ignores a player from the game.",
            max = 0
    )
    @CommandPermissions("pj.ignore")
    public void ignore(PJPlayer sender, @Optional Boolean ignore) throws PJException {
        if (ignore == null) {
            ignore = !sender.isIgnored();
        }
        if (ignore) {
            sender.ignore();
            sender.send(IGNORE_TRUE);
        } else {
            sender.unignore();
            sender.send(IGNORE_FALSE);
        }
    }
}
