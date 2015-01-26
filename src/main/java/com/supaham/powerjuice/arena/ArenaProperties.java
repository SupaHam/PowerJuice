package com.supaham.powerjuice.arena;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.supaham.powerjuice.configuration.serializers.CoordinatesSerializer;
import com.supaham.powerjuice.configuration.serializers.IntRangeSerializer;
import com.supaham.powerjuice.configuration.serializers.ListCoordinatesSerializer;
import com.supaham.powerjuice.configuration.serializers.ListPlatformSerializer;
import com.supaham.powerjuice.configuration.serializers.MaterialDataSerializer;
import com.supaham.powerjuice.configuration.serializers.SoundSerializer;
import com.supaham.powerjuice.misc.Sound;
import com.supaham.powerjuice.platform.PlatformProperties;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.math.IntRange;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.material.MaterialData;
import org.jetbrains.annotations.NotNull;
import pluginbase.config.annotation.Comment;
import pluginbase.config.annotation.Immutable;
import pluginbase.config.annotation.Name;
import pluginbase.config.annotation.NoTypeKey;
import pluginbase.config.annotation.SerializableAs;
import pluginbase.config.annotation.SerializeWith;
import pluginbase.config.properties.PropertiesWrapper;
import pluginbase.minecraft.location.Coordinates;
import pluginbase.minecraft.location.FacingCoordinates;
import pluginbase.minecraft.location.Locations;

/**
 * Represents an {@link Arena}'s properties.
 */
@Getter
@SerializableAs("ArenaProperties")
@NoTypeKey
public final class ArenaProperties extends PropertiesWrapper {

    @Comment("The name of this Arena. This must not be changed AT ALL. Changing this could break the plugin.")
    @Immutable
    @NotNull
    private String name;

    @Comment("This is the \"Human\" name that is used as an alias for this arena")
    @Setter(AccessLevel.PROTECTED)
    private String displayName = "";

    @Comment("This is a small description to describe this Arena.")
    @Setter(AccessLevel.PROTECTED)
    private String description = "";

    @Comment({"This is a string of authors that built this Arena. Examples:", "  SupaHam", "  SupaHam and noobs"})
    @Setter(AccessLevel.PROTECTED)
    private String authors = "";

    @Comment("This is the minimum point of the cuboid boundary.")
    @SerializeWith(CoordinatesSerializer.class)
    private Coordinates min = Locations.NULL_FACING;

    @Comment("This is the maximum point of the cuboid boundary.")
    @SerializeWith(CoordinatesSerializer.class)
    private Coordinates max = Locations.NULL_FACING;

    @Name("bouncy-material")
    @SerializeWith(MaterialDataSerializer.class)
    private MaterialData bouncyMaterial = new MaterialData(Material.HUGE_MUSHROOM_1, (byte) -1);

    @Comment("A list of spawnpoints belonging to this arena.")
    @SerializeWith(ListCoordinatesSerializer.class)
    private List<FacingCoordinates> spawns = new ArrayList<>();

    @Comment("A list of powerup locations belonging to this arena.")
    @SerializeWith(ListCoordinatesSerializer.class)
    private List<Coordinates> powerups = new ArrayList<>();

    @Comment("A list of cameras belonging to this arena.")
    @SerializeWith(ListPlatformSerializer.class)
    private Map<String, PlatformProperties> platforms = new HashMap<>();

    @Name("game-properties")
    private GameProperties gameProperties = new GameProperties();

    private Sounds sounds = new Sounds();

    @Name("weapon-properties")
    private WeaponProperties weaponProperties = new WeaponProperties();

    {
        // Defaults
        PlatformProperties platProps = new PlatformProperties("pink");
        platProps.setDisplayName("Pink");
        platProps.setMaterialData(new MaterialData(Material.STAINED_GLASS, DyeColor.PINK.getData()));
        platProps.setSound(new Sound("pink.sound.here", 1, 1));
        this.platforms.put(platProps.getName(), platProps);
    }

    protected ArenaProperties() {
    }

    protected ArenaProperties(@NotNull String name) {
        this.name = name.toLowerCase();
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
        if (min.equals(nullFacing) || max.equals(nullFacing)) {
            return false;
        }

        if (spawns.size() == 0) {
            return false;
        }
        return true;
    }

    @SerializableAs("GameProperties")
    @NoTypeKey
    @Getter
    public static final class GameProperties {

        @Comment({"The duration (in seconds) of a Game in this Arena."})
        private int duration = 600;

        @Name("points-goal")
        @Comment({"The points required to win the game. Set to -1 for no goal"})
        private int pointsGoal = 5000;

        @Name("kill-reward-points")
        @Comment({"The amount of points to reward a player for getting a kill."})
        private int killRewardPoints = 1;

        @Name("mid-air-kill-reward-points")
        @Comment({"The amount of points to reward a player if their victim was in the air."})
        private int midAirKillRewardPoints = 3;

        @Name("storm-countdown")
        @Comment({"The range of duration (in seconds) that the storm should occur."})
        @SerializeWith(IntRangeSerializer.class)
        private IntRange stormCountdownRange = new IntRange(30, 60);

        @Name("spectate-duration")
        private int specatateDuration = 5;
    }

    @SerializableAs("Sounds")
    @NoTypeKey
    @Getter
    public static final class Sounds {

        @Name("one-second")
        @Comment({"One second countdown sound."})
        @SerializeWith(SoundSerializer.class)
        private Sound oneSecond = new Sound("one.second", 1F, 1F);

        @Name("two-seconds")
        @Comment({"Two second countdown sound."})
        @SerializeWith(SoundSerializer.class)
        private Sound twoSeconds = new Sound("two.seconds", 1F, 1F);

        @Name("three-seconds")
        @Comment({"Three second countdown sound."})
        @SerializeWith(SoundSerializer.class)
        private Sound threeSeconds = new Sound("three.seconds", 1F, 1F);

        @Name("four-seconds")
        @Comment({"Four second countdown sound."})
        @SerializeWith(SoundSerializer.class)
        private Sound fourSeconds = new Sound("four.seconds", 1F, 1F);

        @Name("five-seconds")
        @Comment({"Five second countdown sound."})
        @SerializeWith(SoundSerializer.class)
        private Sound fiveSeconds = new Sound("five.seconds", 1F, 1F);

        @Override
        public String toString() {
            return String.format("one.second: %s\n, "
                                 + "two.seconds: %s\n, "
                                 + "three.seconds: %s\n, "
                                 + "four.seconds: %s\n, "
                                 + "five.seconds: %s\n",
                                 oneSecond, twoSeconds, threeSeconds, fourSeconds, fiveSeconds);
        }
    }

    @SerializableAs("WeaponProperties")
    @NoTypeKey
    @Getter
    public static final class WeaponProperties {

        @Name("super-bow")
        private SuperBow superBow = new SuperBow();

        @SerializableAs("SuperBow")
        @NoTypeKey
        @Getter
        public static final class SuperBow {

            @Name("y-velocity")
            private double yVelocity = 0.3;

            @Name("velocity-multiplier")
            private double velocityMultiplier = 3.3;
        }
    }
}
