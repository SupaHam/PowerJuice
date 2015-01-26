package com.supaham.powerjuice.commands.arena.platform;

import com.supaham.powerjuice.PJException;
import com.supaham.powerjuice.PowerJuicePlugin;
import com.supaham.powerjuice.arena.Arena;
import com.supaham.powerjuice.commands.arena.ArenaCommand;
import com.supaham.powerjuice.platform.PlatformProperties;
import com.supaham.powerjuice.util.Language.Command.Arena.Platform;
import com.supaham.powerjuice.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.supaham.powerjuice.util.Language.Command.ILLEGAL_CHARS;

public abstract class PlatformCommand extends ArenaCommand {

    protected PlatformCommand(@NotNull PowerJuicePlugin plugin) {
        super(plugin, plugin.getArenaManager());
    }

    public void isValidName(@NotNull String name) throws PJException {
        if (!StringUtil.isASCII(name)) {
            throw new PJException(ILLEGAL_CHARS.getParsedMessage(name));
        }
    }

    public PlatformProperties getPlatform(@NotNull Arena arena, @Nullable String name) throws PJException {
        if (name == null) {
            throw new PJException("Please provide a Platform name.");
        }
        PlatformProperties properties = arena.getPlatform(name);
        if (properties == null) {
            throw new PJException(Platform.NOT_FOUND.getParsedMessage(name, arena.getName()));
        }
        return properties;
    }
}
