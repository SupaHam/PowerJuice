package com.supaham.powerjuice.commands.gamersession;

import com.supaham.powerjuice.PowerJuicePlugin;
import org.jetbrains.annotations.NotNull;


/**
 * Game commands.
 */
public class GamerSessionCommands extends GamerSessionCommand {

    public GamerSessionCommands(@NotNull PowerJuicePlugin plugin) {
        super(plugin, plugin.getGameManager());
    }
}
