package com.supaham.powerjuice.configuration.serializers;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pluginbase.config.serializers.Serializer;
import pluginbase.minecraft.location.Coordinates;
import pluginbase.minecraft.location.FacingCoordinates;
import pluginbase.minecraft.location.Locations;

public class CoordinatesSerializer implements Serializer<Coordinates> {

    @Override
    @Nullable
    public Object serialize(@Nullable final Coordinates coordinates) {
        if (coordinates == null) {
            return Locations.NULL_FACING;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(coordinates.getX()).append(" ");
        sb.append(coordinates.getY()).append(" ");
        sb.append(coordinates.getZ());
        if (coordinates instanceof FacingCoordinates) {
            FacingCoordinates coords = (FacingCoordinates) coordinates;
            if (coords.getYaw() != 0 || coords.getPitch() != 0) {
                sb.append(" ").append(coords.getYaw()).append(" ");
                sb.append(coords.getPitch());
            }
        }
        return sb.toString();
    }

    @Override
    @Nullable
    public Coordinates deserialize(@Nullable Object serialized, @NotNull Class wantedType)
            throws IllegalArgumentException {
        if (serialized instanceof String) {
            String[] split = serialized.toString().split(" ");
            double x = Double.valueOf(split[0]);
            double y = Double.valueOf(split[1]);
            double z = Double.valueOf(split[2]);
            if (split.length > 3) {
                float yaw = Float.valueOf(split[3]);
                float pitch = Float.valueOf(split[4]);
                return Locations.getFacingCoordinates(x, y, z, pitch, yaw);
            }
            return Locations.getCoordinates(x, y, z);
        } else {
            throw new IllegalArgumentException("Cannot deserialize coordinates from data: " + serialized);
        }
    }
}
