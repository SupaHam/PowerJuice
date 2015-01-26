package com.supaham.powerjuice.commands.arena;

import java.util.Set;
import java.util.stream.Collectors;

import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.regions.Region;
import com.supaham.powerjuice.PJException;
import com.supaham.powerjuice.PowerJuicePlugin;
import com.supaham.powerjuice.arena.Arena;
import com.supaham.powerjuice.arena.ArenaManager;
import com.supaham.powerjuice.commands.PJCommand;
import com.supaham.powerjuice.util.StringUtil;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.supaham.powerjuice.util.Language.Command.Arena.NOT_FOUND;
import static com.supaham.powerjuice.util.Language.Command.ILLEGAL_CHARS;

public abstract class ArenaCommand extends PJCommand {

    public final ArenaManager arenaManager;

    protected ArenaCommand(@NotNull PowerJuicePlugin plugin, @NotNull ArenaManager arenaManager) {
        super(plugin);
        this.arenaManager = arenaManager;
    }

    public void isValidName(@NotNull String name) throws PJException {
        if (!StringUtil.isASCII(name)) {
            throw new PJException(ILLEGAL_CHARS.getParsedMessage(name));
        }
    }

    /**
     * Finds an {@link Arena} that a {@link Player} is standing in.
     *
     * @param player player to check
     * @return the one Arena the player is standing in
     * @throws PJException thrown if the {@code player} is standing in none/multiple arenas
     */
    public Arena findArenaStandingIn(Player player) throws PJException {
        return findArenaByLocation(player.getLocation(),
                                   "You're not standing in an arena.", "You're standing in multiple arenas... ");
    }

    /**
     * Finds an {@link Arena} that a {@link Player} is standing in.
     *
     * @param location    location to check arena in
     * @param nonFound    the exception message if no arenas were found
     * @param moreThanOne the exception message if two or more arenas were found
     * @return the one Arena the player is standing in
     * @throws PJException thrown if the {@code player} is standing in none/multiple arenas
     */
    public Arena findArenaByLocation(@NotNull Location location, String nonFound,
                                     String moreThanOne) throws PJException {
        Set<Arena> arenas = arenaManager.getArenas().values().stream().filter(arena -> arena.contains(location))
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

    public Arena getArena(@NotNull World world, @NotNull Region region, @Nullable String arenaName)
            throws PJException {
        Vector min = region.getMinimumPoint();
        Location location = new Location(world, min.getX(), min.getY(), min.getZ()); // meh
        return arenaName == null ?
               findArenaByLocation(location, "No Arenas found in your selection",
                                   "There are more than one Arenas in your selection...") : getArena(arenaName);
    }

    public Arena getArena(String arenaName) throws PJException {
        return getArena(arenaName, true);
    }

    /**
     * Gets an {@link Arena} by name.
     *
     * @param arenaName   name of the {@link Arena} to get.
     * @param throwIfNull whether to throw a {@link PJException} if the arena doesn't exist.
     * @return the retrieved arena
     * @throws PJException thrown if {@code throwIfNull} is true and the retrieved arena is null
     */
    @Contract
    public Arena getArena(@NotNull String arenaName, boolean throwIfNull) throws PJException {
        isValidName(arenaName);

        Arena arena = arenaManager.getArena(arenaName);
        if (throwIfNull && arena == null) {
            throw new PJException(NOT_FOUND.getParsedMessage(arenaName));
        }
        return arena;
    }

    /**
     * Checks if an Arena exists in the {@link ArenaManager}.
     *
     * @param arenaName arena name to check
     * @return whether the {@code arena} exists in the {@link ArenaManager}
     */
    public boolean exists(String arenaName) {
        return arenaManager.hasArena(arenaName);
    }
}
