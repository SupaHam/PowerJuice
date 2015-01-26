package com.supaham.powerjuice.game;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.supaham.powerjuice.arena.Arena;
import com.supaham.powerjuice.arena.ArenaProperties;
import com.supaham.powerjuice.misc.Sound;
import com.supaham.powerjuice.platform.Platform;
import com.supaham.powerjuice.util.LocationUtil;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang.math.IntRange;
import org.bukkit.Location;
import org.bukkit.material.MaterialData;
import pluginbase.logging.PluginLogger;

/**
 * Represents a {@link Game}'s properties.
 */
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Getter(AccessLevel.PROTECTED)
public class GameProperties {
    
    private final Game game;

    private MaterialData bouncyMaterial;
    private List<Location> powerupLocations = new ArrayList<>();
    private List<Platform> platforms = new ArrayList<>();
    private long startTime;
    @Setter(AccessLevel.PROTECTED)
    private long endTime;
    
    private int pointsGoal;
    private int killsRewardPoints;
    private int midAirKillRewardPoints;
    private IntRange stormCountdown;
    private int spectateTicks;
    
    private Sounds sounds = new Sounds();
    @Getter
    private WeaponProperties weapons = new WeaponProperties();

    private int sneakLaunchCD = 60;
    
    protected void init() {
        Arena arena = game.getArena();
        ArenaProperties props = arena.getProperties();

        PluginLogger log = this.game.getPlugin().getLog();
        
        log.fine("bouncy-material: %s", props.getBouncyMaterial());
        this.bouncyMaterial = props.getBouncyMaterial();
        
        log.fine("powerupLocations: %s", props.getPowerups());
        this.powerupLocations.addAll(props.getPowerups().stream().map(coordinates -> LocationUtil
                .coordsToLocation(coordinates, game.getWorld())).collect(Collectors.toList()));
        
        log.fine("total platforms: (%d)", props.getPlatforms().size());
        log.finer("platforms: (%s)", props.getPlatforms());
        this.platforms.addAll(props.getPlatforms().values().stream().map(Platform::new).collect(Collectors.toList()));
        
        ArenaProperties.GameProperties gameProps = props.getGameProperties();
        log.fine("points-goal: %d", gameProps.getPointsGoal());
        this.pointsGoal = gameProps.getPointsGoal();
        
        log.fine("kills-reward-points: %d", gameProps.getKillRewardPoints());
        this.killsRewardPoints = gameProps.getKillRewardPoints();
        
        log.fine("mid-air-kill-reward-points: %d", gameProps.getMidAirKillRewardPoints());
        this.midAirKillRewardPoints = gameProps.getMidAirKillRewardPoints();
        
        IntRange range = gameProps.getStormCountdownRange();
        log.fine("storm-countdown: %s", range);
        this.stormCountdown = new IntRange((int) range.getMinimumNumber() * 20, (int) range.getMaximumNumber() * 20);
        
        log.fine("spectate-duration: %d", gameProps.getSpecatateDuration());
        this.spectateTicks = gameProps.getSpecatateDuration() * 20;
        
        log.fine("sounds: %s", props.getSounds());
        this.sounds.oneSecond = props.getSounds().getOneSecond();
        this.sounds.twoSeconds = props.getSounds().getTwoSeconds();
        this.sounds.threeSeconds = props.getSounds().getThreeSeconds();
        this.sounds.fourSeconds = props.getSounds().getFourSeconds();
        this.sounds.fiveSeconds = props.getSounds().getFiveSeconds();

        double yVelocity = props.getWeaponProperties().getSuperBow().getYVelocity();
        log.fine("weapons.superBow.yVelocity: %f", yVelocity);
        this.weapons.superBow.yVelocity = yVelocity;
        
        double velocityMultiplier = props.getWeaponProperties().getSuperBow().getVelocityMultiplier();
        log.fine("weapons.superBow.velocityMultiplier: %f", velocityMultiplier);
        this.weapons.superBow.velocityMultiplier = velocityMultiplier;
    }
    
    protected void start() {
        Arena arena = game.getArena();
        
        this.startTime = System.currentTimeMillis();
        this.endTime = System.currentTimeMillis() + 
                       TimeUnit.SECONDS.toMillis(arena.getProperties().getGameProperties().getDuration());
    }
    
    protected void stop() {
    }

    @Getter(AccessLevel.PROTECTED)
    public static final class Sounds {

        protected Sound oneSecond = new Sound("one.second", 1F, 1F);

        protected Sound twoSeconds = new Sound("two.seconds", 1F, 1F);

        protected Sound threeSeconds = new Sound("three.seconds", 1F, 1F);

        protected Sound fourSeconds = new Sound("four.seconds", 1F, 1F);

        private Sound fiveSeconds = new Sound("five.seconds", 1F, 1F);
    }

    public static final class WeaponProperties {
        @Getter
        private SuperBow superBow = new SuperBow();

        @Getter
        public final class SuperBow {
            private double yVelocity = 0.3;
            private double velocityMultiplier = 3.3;
        }
    }
}
