package com.supaham.powerjuice.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.Validate;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.material.MaterialData;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pluginbase.minecraft.location.Coordinates;
import pluginbase.minecraft.location.FacingCoordinates;
import pluginbase.minecraft.location.Locations;

/**
 * Contains Location related methods.
 */
public class LocationUtil {

    /**
     * Converts a {@link Coordinates} to a {@link Location}.
     *
     * @param coords coordinates to convert
     * @param world  world to construct the location in.
     * @return {@link Location}
     */
    public static Location coordsToLocation(@NotNull Coordinates coords, @NotNull World world) {
        float yaw = 0F;
        float pitch = 0F;
        if (coords instanceof FacingCoordinates) {
            yaw = ((FacingCoordinates) coords).getYaw();
            pitch = ((FacingCoordinates) coords).getPitch();
        }
        return new Location(world, coords.getX(), coords.getY(), coords.getZ(), yaw, pitch);
    }

    /**
     * Converts a {@link Coordinates} to a {@link Location}.
     *
     * @param coords coordinates to convert
     * @param world  world to construct the location in.
     * @return {@link Location}
     */
    public static Location facingCoordsToLocation(@NotNull FacingCoordinates coords, @NotNull World world) {
        return new Location(world, coords.getX(), coords.getY(), coords.getZ(), coords.getYaw(), coords.getPitch());
    }

    /**
     * Converts a {@link Coordinates} to a {@link Vector}.
     *
     * @param coords coordinates to convert
     * @return converted {@link Vector}
     */
    public static Vector coordsToVector(@NotNull Coordinates coords) {
        return new Vector(coords.getX(), coords.getY(), coords.getZ());
    }

    /**
     * Converts a {@link Coordinates} to a {@link Vector}.
     *
     * @param vector coordinates to convert
     * @return converted {@link Vector}
     */
    public static Vector weVectorToVector(@NotNull com.sk89q.worldedit.Vector vector) {
        return new Vector(vector.getX(), vector.getY(), vector.getZ());
    }

    public static Coordinates locationToCoords(@NotNull Location loc) {
        return Locations.getCoordinates(loc.getX(), loc.getY(), loc.getZ());
    }

    /**
     * Converts a {@link Vector} to a {@link Coordinates}.
     *
     * @param vector vector to convert
     * @return converted {@link Coordinates}
     */
    public static Coordinates vectorToCoords(@NotNull Vector vector) {
        return Locations.getCoordinates(vector.getX(), vector.getY(), vector.getZ());
    }

    public static FacingCoordinates locationToFacingCoords(@NotNull Location loc) {
        return Locations.getFacingCoordinates(loc.getX(), loc.getY(), loc.getZ(), loc.getPitch(), loc.getYaw());
    }

    /**
     * Checks if two {@link Coordinates} are within the same block. If both of them are null, true is returned.
     *
     * @param o  first {@link Coordinates} to check
     * @param o2 second {@link Coordinates} to check
     * @return whether {@code o} and {@code o2} are the same block
     */
    public static boolean sameBlock(@Nullable Coordinates o, @Nullable Coordinates o2) {
        return o == null && o2 == null ||
               (o != null && o2 != null) && (o.getBlockX() == o2.getBlockX()) && (o.getBlockY() == o2.getBlockY()) &&
               (o.getBlockZ() == o2.getBlockZ());
    }

    /**
     * @see LocationUtil#isWithin(Vector, Vector, Vector)
     */
    public static boolean isWithin(@NotNull Coordinates test, Coordinates min, Coordinates max) {
        return LocationUtil.isWithin(coordsToVector(test), coordsToVector(min), coordsToVector(max));
    }

    /**
     * Checks if a {@link Location} is within two other {@link Location}s.
     *
     * @param test location to test.
     * @param min  minimum point of a cuboid region.
     * @param max  maximum point of a cuboid region.
     * @return whether the {@code test} location is within the {@code min} and {@code max} locations.
     * @see #isWithin(Vector, Vector, Vector)
     */
    public static boolean isWithin(Location test, Location min, Location max) {
        return isWithin(test.toVector(), min.toVector(), max.toVector());
    }

    /**
     * Checks if a {@link Vector} is within two other {@link Vector}s.
     *
     * @param test vector to test.
     * @param min  minimum point of a cuboid region.
     * @param max  maximum point of a cuboid region.
     * @return whether the {@code test} vector is within the {@code min} and {@code max} vectors.
     */
    public static boolean isWithin(Vector test, Vector min, Vector max) {
        Validate.notNull(test);
        Validate.notNull(min);
        Validate.notNull(max);

        double x = test.getX();
        double y = test.getY();
        double z = test.getZ();

        return x >= min.getBlockX() && x < max.getBlockX() + 1 &&
               y >= min.getBlockY() && y < max.getBlockY() + 1 &&
               z >= min.getBlockZ() && z < max.getBlockZ() + 1;
    }

    /**
     * Gets the first block above a {@link Location}.
     *
     * @param check block to check
     * @return the y coordinate of the first block above the location
     */
    public static int getFirstBlockAbove(Location check) {
        return getFirstBlockAbove(check, new ArrayList<>());
    }

    /**
     * Gets the first block above a {@link Location}.
     *
     * @param check  block to check
     * @param ignore the {@link MaterialData}s to ignore from the check
     * @return the y coordinate of the first block above the location
     */
    public static int getFirstBlockAbove(Location check, List<MaterialData> ignore) {
        Validate.isTrue(check.getBlockY() <= 256, "y location can not be larger than 255");
        Validate.notNull(ignore);
        int x = check.getBlockX();
        int y = check.getBlockY();
        int z = check.getBlockZ();
        outer:
        for (; y < 256; y++) {
            Block b = check.getWorld().getBlockAt(x, y, z);
            if (b.getType().equals(Material.AIR)) {
                continue;
            }
            for (MaterialData data : ignore) {
                if (!MaterialUtil.same(b, data)) { // This block is the first block above their head
                    continue outer;
                }
            }
            break;
        }
        return y;
    }

    /**
     * Returns a {@link Location} as a {@link String} without the world.
     *
     * @param location location
     * @return string
     */
    public static String locToString(Location location) {
        if (location == null) {
            return null;
        }
        return location.getX() + ", " + location.getY() + ", " + location.getZ(); 
    }
}
