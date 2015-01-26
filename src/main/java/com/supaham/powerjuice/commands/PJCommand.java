package com.supaham.powerjuice.commands;

import com.supaham.powerjuice.PJException;
import com.supaham.powerjuice.PowerJuicePlugin;
import com.supaham.powerjuice.players.PJPlayer;
import org.jetbrains.annotations.NotNull;
import pluginbase.messages.messaging.Messager;

import static com.supaham.powerjuice.util.Language.PLAYER_NOT_ONLINE;

/**
 * Represents a Base command for {@link PowerJuicePlugin}.
 */
public abstract class PJCommand {

    protected PowerJuicePlugin plugin;

    protected PJCommand(@NotNull PowerJuicePlugin plugin) {
        this.plugin = plugin;
    }

    public Messager getMessager() {
        return plugin.getPluginBase().getMessager();
    }

    public PJPlayer getPJPlayer(String playerName) throws PJException {
        PJPlayer pjPlayer = plugin.getPlayerManager().getPJPlayer(playerName);
        if(pjPlayer == null) {
            throw new PJException(PLAYER_NOT_ONLINE.getParsedMessage(playerName));
        }
        return pjPlayer;
    }
}
