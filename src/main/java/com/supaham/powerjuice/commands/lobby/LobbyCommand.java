package com.supaham.powerjuice.commands.lobby;

import com.supaham.powerjuice.PJException;
import com.supaham.powerjuice.PowerJuicePlugin;
import com.supaham.powerjuice.commands.PJCommand;
import com.supaham.powerjuice.lobby.Lobby;
import com.supaham.powerjuice.lobby.LobbyManager;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static com.supaham.powerjuice.util.Language.Command.Lobby.NOT_CREATED;

public abstract class LobbyCommand extends PJCommand {

    public final LobbyManager lobbyManager;

    protected LobbyCommand(@NotNull PowerJuicePlugin plugin, @NotNull LobbyManager lobbyManager) {
        super(plugin);
        this.lobbyManager = lobbyManager;
    }

    /**
     * Finds a {@link Lobby} that a {@link Player} is standing in.
     *
     * @return the Lobby the player is standing in
     * @throws PJException thrown if the {@code player} is not standing in a Lobby
     */
    public Lobby checkStandingInLobby(Player player) throws PJException {
        return findLobbyByLocation(player.getLocation(), "You're not standing within the Lobby.");
    }

    /**
     * Finds a {@link Lobby} that a {@link Player} is standing in.
     *
     * @param location location to check lobby in
     * @param notFound the exception message if the lobby was not found
     * @return the Lobby the player is standing in
     * @throws PJException thrown if the {@code player} is not standing in a Lobby
     */
    public Lobby findLobbyByLocation(@NotNull Location location, String notFound) throws PJException {
        Lobby lobby = getLobby();
        
        if (!lobby.contains(location)) {
            throw new PJException(notFound);
        }

        return lobby;
    }

    public Lobby getLobby() throws PJException {
        Lobby lobby = this.lobbyManager.getLobby();
        if (lobby == null) {
            throw new PJException(NOT_CREATED.getParsedMessage());
        }
        return lobby;
    }
}
