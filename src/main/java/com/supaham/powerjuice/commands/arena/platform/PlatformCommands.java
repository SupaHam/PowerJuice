package com.supaham.powerjuice.commands.arena.platform;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import com.sk89q.worldedit.util.command.parametric.Optional;
import com.supaham.powerjuice.PJException;
import com.supaham.powerjuice.PowerJuicePlugin;
import com.supaham.powerjuice.arena.Arena;
import com.supaham.powerjuice.platform.PlatformProperties;
import com.supaham.powerjuice.players.PJPlayer;
import com.supaham.powerjuice.util.Language.Command.Arena.Platform;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PlatformCommands extends PlatformCommand {

    public PlatformCommands(@NotNull PowerJuicePlugin plugin) {
        super(plugin);
    }

    @Command(
            aliases = {"create"},
            desc = "Create a Platform.",
            usage = "[arena] <platformName>",
            help = "Create a Platform.",
            min = 1,
            max = 2
    )
    @CommandPermissions("pj.arena.platform.create")
    public void create(PJPlayer sender, @Optional String arenaName, String platformName) throws PJException {
        Player player = sender.getPlayer();
        isValidName(platformName);
        platformName = platformName.toLowerCase();
        Arena arena = arenaName == null ? findArenaStandingIn(player) : getArena(arenaName);
        arena.addPlatform(new PlatformProperties(platformName));
        arena.save();
        Platform.CREATE_SUCCESS.send(player, platformName, arena.getName());
    }

    @Command(
            aliases = {"delete"},
            desc = "Delete a Platform.",
            usage = "[arena] <platformName>",
            help = "Delete a Platform.",
            min = 1,
            max = 2
    )
    @CommandPermissions("pj.arena.platform.delete")
    public void delete(PJPlayer sender, @Optional String arenaName, String platformName) throws PJException {
        Player player = sender.getPlayer();
        isValidName(platformName);
        platformName = platformName.toLowerCase();
        Arena arena = arenaName == null ? findArenaStandingIn(player) : getArena(arenaName);
        PlatformProperties removed = arena.removePlatform(platformName);
        arena.save();
        if(removed == null) {
            Platform.NOT_FOUND.send(player, platformName, arena.getName());
        } else {
            Platform.DELETE_SUCCESS.send(player, platformName, arena.getName());
        }
    }
}
