package com.supaham.powerjuice.commands;

import java.util.Set;
import java.util.stream.Collectors;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.util.command.parametric.ArgumentStack;
import com.sk89q.worldedit.util.command.parametric.BindingBehavior;
import com.sk89q.worldedit.util.command.parametric.BindingHelper;
import com.sk89q.worldedit.util.command.parametric.BindingMatch;
import com.sk89q.worldedit.util.command.parametric.ParameterException;
import com.supaham.powerjuice.PJException;
import com.supaham.powerjuice.PowerJuicePlugin;
import com.supaham.powerjuice.arena.Arena;
import com.supaham.powerjuice.arena.ArenaManager;
import com.supaham.powerjuice.configuration.serializers.MaterialDataSerializer;
import com.supaham.powerjuice.game.GameManager;
import com.supaham.powerjuice.game.GamerSession;
import com.supaham.powerjuice.players.PJPlayer;
import com.supaham.powerjuice.worldedit.annotations.Selection;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.material.MaterialData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PJBinding extends BindingHelper {

    private final PowerJuicePlugin plugin;


    public static <T> T checkNotNull(T object, String message) throws ParameterException {
        if (object == null) {
            throw new ParameterException(message);
        }
        return object;
    }

    /**
     * Create a new instance.
     *
     * @param plugin the WorldEdit instance to bind to
     */
    public PJBinding(@NotNull PowerJuicePlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Gets an {@link GamerSession} from an {@link ArgumentStack}.
     *
     * @param context the context
     * @return a {@link GamerSession}
     * @throws ParameterException on error
     */
    @BindingMatch(type = GamerSession.class,
                  behavior = BindingBehavior.PROVIDES)
    @Nullable
    public GamerSession getGamerSession(ArgumentStack context) throws ParameterException {
        GameManager mgr = plugin.getGameManager();
        if (mgr == null) return null;
        // TODO check game state
        return mgr.getCurrentGame().getSession(getPlayer(context));
    }

    /**
     * Gets an {@link PJPlayer} from an {@link ArgumentStack}.
     *
     * @param context the context
     * @return a {@link PJPlayer}
     * @throws ParameterException on error
     */
    @BindingMatch(type = PJPlayer.class,
                  behavior = BindingBehavior.PROVIDES)
    public PJPlayer getPJPlayer(ArgumentStack context) throws ParameterException {
        return context.getContext().getLocals().get(PJPlayer.class);
    }

    /**
     * Gets an {@link Player} from an {@link ArgumentStack}.
     *
     * @param context the context
     * @return a {@link Player}
     * @throws ParameterException on error
     */
    @BindingMatch(type = Player.class,
                  behavior = BindingBehavior.PROVIDES)
    public Player getPlayer(ArgumentStack context) throws ParameterException {
        CommandSender sender = getCommandSender(context);
        if (sender == null) {
            throw new ParameterException("No player to get.");
        } else if (sender instanceof Player) {
            return (Player) sender;
        } else {
            throw new ParameterException("Sender is not a player.");
        }
    }

    /**
     * Gets an {@link Player} from an {@link ArgumentStack}.
     *
     * @param context the context
     * @return a {@link Player}
     * @throws ParameterException on error
     */
    @BindingMatch(type = CommandSender.class,
                  behavior = BindingBehavior.PROVIDES)
    public CommandSender getCommandSender(ArgumentStack context) throws ParameterException {
        return context.getContext().getLocals().get(CommandSender.class);
    }

    /**
     * Gets an {@link Arena} from an {@link ArgumentStack}.
     *
     * @param context the context
     * @return an {@link Arena} instance
     * @throws ParameterException on error
     */
    @BindingMatch(type = Arena.class,
                  behavior = BindingBehavior.CONSUMES,
                  consumedCount = -1)
    public Arena getArena(ArgumentStack context) throws ParameterException, PJException {
        String input = context.next();
        Arena arena = null;
        ArenaManager mgr = plugin.getArenaManager();
        if (input == null || input.isEmpty()) {
            Player player = null;
            try {
                player = getPlayer(context);
            } catch (ParameterException ignored) {

            }
            if (player != null) {
                arena = findArenaByLocation(player.getLocation(), "", "");
            }
        } else {
            arena = checkNotNull(mgr.getArena(input.toLowerCase()), "'" + input + "' is not a valid arena.");
        }
        checkNotNull(arena, "Please specify an arena.");
        return arena;
    }

    /**
     * Gets a {@link MaterialData} from an {@link ArgumentStack}.
     *
     * @param context the context
     * @return a {@link MaterialData} instance
     * @throws ParameterException on error
     */
    @BindingMatch(type = MaterialData.class,
                  behavior = BindingBehavior.CONSUMES,
                  consumedCount = 1)
    public MaterialData getMaterialData(ArgumentStack context) throws ParameterException {
        String input = checkNotNull(context.next(), "Please specify an arena.");
        return checkNotNull(new MaterialDataSerializer().deserialize(input, MaterialData.class),
                            "'" + input + "' is not a valid arena.");
    }

    // WORLDEDIT

    /**
     * Gets a selection from a {@link ArgumentStack}.
     *
     * @param context   the context
     * @param selection the annotation
     * @return a selection
     * @throws IncompleteRegionException if no selection is available
     * @throws ParameterException        on other error
     */
    @BindingMatch(classifier = Selection.class,
                  type = Region.class,
                  behavior = BindingBehavior.PROVIDES)
    public Object getSelection(ArgumentStack context, Selection selection)
            throws ParameterException, IncompleteRegionException, PJException {
        Player player = getPlayer(context);
        com.sk89q.worldedit.bukkit.selections.Selection sel = plugin.getWorldEdit().getSelection(player);
        if (sel == null) {
            throw new IncompleteRegionException();
        }

        if (selection.value() != null && selection.value().length > 0) {
            boolean found = false;
            for (Class<? extends com.sk89q.worldedit.bukkit.selections.Selection> clazz : selection.value()) {
                if (sel.getClass().isAssignableFrom(clazz)) found = true;
            }
            if (!found) {
                throw new PJException("Your selection is not supported.");
            }
        }

        return sel.getRegionSelector().getRegion();
    }

    /**
     * Finds an {@link Arena} that a {@link Player} is standing in.
     *
     * @param location    location to check arena in
     * @param nonFound    the exception message if no arenas were found
     * @param moreThanOne the exception message if two or more arenas were found
     * @return the one {@link Arena} the player is standing in
     * @throws PJException thrown if the {@code player} is standing in none/multiple arenas
     */
    public Arena findArenaByLocation(@NotNull Location location, String nonFound,
                                     String moreThanOne) throws PJException {
        ArenaManager mgr = plugin.getArenaManager();
        Set<Arena> arenas = mgr.getArenas().values().stream().filter(arena -> arena.contains(location))
                .collect(Collectors.toSet());

        if (arenas.size() == 0) {
            throw new PJException(nonFound);
        } else if (arenas.size() > 1) {
            StringBuilder sb = new StringBuilder();
            for (Arena arena : arenas) {
                sb.append(arena.getName()).append(", ");
            }
            sb.setLength(sb.length() - 2);
            throw new PJException(moreThanOne + sb.toString());
        }

        return arenas.iterator().next();
    }
}
