package com.supaham.powerjuice.lobby;

import java.util.ArrayList;
import java.util.List;

import com.supaham.powerjuice.configuration.serializers.CoordinatesSerializer;
import com.supaham.powerjuice.configuration.serializers.ListCoordinatesSerializer;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import pluginbase.config.annotation.Comment;
import pluginbase.config.annotation.SerializableAs;
import pluginbase.config.annotation.SerializeWith;
import pluginbase.config.properties.PropertiesWrapper;
import pluginbase.minecraft.location.Coordinates;
import pluginbase.minecraft.location.FacingCoordinates;
import pluginbase.minecraft.location.Locations;

/**
 * Represents an {@link Lobby}'s properties.
 */
@Getter
@SerializableAs("LobbyProperties")
public class LobbyProperties extends PropertiesWrapper {
    
    @Comment({"This is a string of authors that built this Lobby. Examples:", "  SupaHam", "  SupaHam and noobs"})
    @Setter(AccessLevel.PROTECTED)
    private String authors = "";

    @Comment({"The duration of the countdown."})
    private int countdown = 60;
    
    @Comment("This is the minimum point of the cuboid boundary.")
    @SerializeWith(CoordinatesSerializer.class)
    private Coordinates min = Locations.NULL_FACING;

    @Comment("This is the maximum point of the cuboid boundary.")
    @SerializeWith(CoordinatesSerializer.class)
    private Coordinates max = Locations.NULL_FACING;

    @Comment("A list of spawnpoints belonging to this arena.")
    @SerializeWith(ListCoordinatesSerializer.class)
    private List<FacingCoordinates> spawns = new ArrayList<>();

    protected LobbyProperties() {
    }

    protected LobbyProperties(@NotNull Coordinates min, @NotNull Coordinates max) {
        setBoundaries(min, max);
    }

    protected void setBoundaries(@NotNull Coordinates min, @NotNull Coordinates max) {
        int minX = min.getBlockX();
        int minY = min.getBlockY();
        int minZ = min.getBlockZ();

        int maxX = max.getBlockX();
        int maxY = max.getBlockY();
        int maxZ = max.getBlockZ();

        if (maxX < minX) minX = maxX;
        if (maxY < minY) minY = maxY;
        if (maxZ < minZ) minZ = maxZ;

        if (minX > maxX) maxX = minX;
        if (minY > maxY) maxY = minY;
        if (minZ > maxZ) maxZ = minZ;
        this.min = Locations.getCoordinates(minX, minY, minZ);
        this.max = Locations.getCoordinates(maxX, maxY, maxZ);
    }
    
    public boolean isPlayable() {
        FacingCoordinates nullFacing = Locations.NULL_FACING;
        if(min.equals(nullFacing) || max.equals(nullFacing)) {
            return false;
        }
        
        if (spawns.size() == 0) {
            return false;
        }
        return true;
    }
}
