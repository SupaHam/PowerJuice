package com.supaham.powerjuice.commands.arena.platform;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import com.sk89q.worldedit.util.command.binding.Range;
import com.sk89q.worldedit.util.command.parametric.Optional;
import com.supaham.powerjuice.PJException;
import com.supaham.powerjuice.PowerJuicePlugin;
import com.supaham.powerjuice.arena.Arena;
import com.supaham.powerjuice.misc.Sound;
import com.supaham.powerjuice.players.PJPlayer;
import com.supaham.powerjuice.util.Language.Command.Arena.Platform;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.material.MaterialData;
import org.jetbrains.annotations.NotNull;

public class PlatformSetCommands extends PlatformCommand {

    public PlatformSetCommands(@NotNull PowerJuicePlugin plugin) {
        super(plugin);
    }

    @Command(
            aliases = {"display-name", "name"},
            desc = "Sets a Platform's display name.",
            usage = "[arena] <platformName> <displayName>",
            help = "Sets a Platform's display name.",
            min = 2,
            max = 3
    )
    @CommandPermissions("pj.arena.platform.set.display-name")
    public void dispayName(PJPlayer sender, @Optional String arenaName, String platformName, String displayName)
            throws PJException {
        Player player = sender.getPlayer();
        isValidName(platformName);
        displayName = ChatColor.translateAlternateColorCodes('&', displayName);
        Arena arena = arenaName == null ? findArenaStandingIn(player) : getArena(arenaName);
        getPlatform(arena, platformName).setDisplayName(displayName);
        arena.save();
        Platform.SET_DISPLAY_NAME_SUCCESS.send(player, platformName, displayName);
    }

    @Command(
            aliases = {"material", "block"},
            desc = "Sets a Platform's material type.",
            usage = "[arena] <platformName> <material>",
            help = "Sets a Platform's material type.",
            min = 2,
            max = 3
    )
    @CommandPermissions("pj.arena.platform.set.material")
    public void material(PJPlayer sender, @Optional String arenaName, String platformName, MaterialData data)
            throws PJException {
        Player player = sender.getPlayer();
        isValidName(platformName);
        Arena arena = arenaName == null ? findArenaStandingIn(player) : getArena(arenaName);
        getPlatform(arena, platformName).setMaterialData(data);
        arena.save();
        Platform.SET_MATERIAL_SUCCESS.send(player, platformName, data.getItemType() + ":" + data.getData());
    }
    
    @Command(
            aliases = {"sound"},
            desc = "Sets a Platform's sound.",
            usage = "[arena] <platformName> <sound> [volume] [pitch]",
            help = "Sets a Platform's sound.",
            min = 2,
            max = 5
    )
    @CommandPermissions("pj.arena.platform.set.sound")
    public void sound(PJPlayer sender, @Optional Arena arena, String platformName, String sound,
                      @Range(min = 0, max = 2) @Optional("1") float volume,
                      @Range(min = 0, max = 2) @Optional("1") float pitch) throws PJException {
        Player player = sender.getPlayer();
        player.sendMessage(arena + " " + platformName + " " + sound + " " + volume + " " + pitch);
        isValidName(platformName);
//        Arena arena = arena == null ? findArenaStandingIn(player) : getArena(arena);
        getPlatform(arena, platformName).setSound(new Sound(sound, volume, pitch));
        arena.save();
        Platform.SET_SOUND_SUCCESS.send(player, platformName, sound);
    }
}
