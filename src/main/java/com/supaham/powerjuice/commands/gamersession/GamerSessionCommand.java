package com.supaham.powerjuice.commands.gamersession;

import com.supaham.powerjuice.PowerJuicePlugin;
import com.supaham.powerjuice.commands.game.GameCommand;
import com.supaham.powerjuice.game.GameManager;
import org.jetbrains.annotations.NotNull;

public abstract class GamerSessionCommand extends GameCommand {

    public final GameManager gameManager;
    
    protected GamerSessionCommand(@NotNull PowerJuicePlugin plugin, @NotNull GameManager gameManager) {
        super(plugin, plugin.getGameManager());
        this.gameManager = gameManager;
    }
}
