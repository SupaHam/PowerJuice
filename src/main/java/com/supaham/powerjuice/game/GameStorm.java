package com.supaham.powerjuice.game;

import java.util.ArrayList;
import java.util.List;

import com.supaham.powerjuice.platform.Platform;
import com.supaham.powerjuice.util.CollectionUtil;
import com.supaham.powerjuice.util.NumberUtil;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.math.IntRange;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static com.supaham.powerjuice.util.Language.Game.GO_TO_PLATFORM;

/**
 * Represents the storm mechanic in the {@link Game}.
 */
@RequiredArgsConstructor
@Getter
public class GameStorm {

    private final Game game;
    private final List<Player> victims = new ArrayList<>();
    private Platform nextPlatform;
    private int nextStorm = 0;

    protected void init() {
        this.game.getPlugin().getLog().fine("Initializing GameStorm.");
        setNextStorm();
    }

    protected void tick(boolean secondTick) {
        if (this.nextStorm-- > 0) {
            if (this.nextStorm % 20 != 0) {
                return;
            }
            if (this.nextStorm == 120) { // Announce platform in chat notification
                for (GamerSession session : game.getPlayingSessions()) {
                    Player player = session.getBukkitPlayer();
                    nextPlatform.getProperties().getSound().play(player);
                    GO_TO_PLATFORM.send(player, nextPlatform.getProperties().getDisplayName());
                }
            } else if (this.nextStorm == 100) { // Play 5 second sound
                for (GamerSession session : game.getPlayingSessions()) {
                    Player player = session.getBukkitPlayer();
                    game.getProperties().getSounds().getFiveSeconds().play(player);
                }
            } else if (this.nextStorm == 80) { // Play 4 second sound
                for (GamerSession session : game.getPlayingSessions()) {
                    Player player = session.getBukkitPlayer();
                    game.getProperties().getSounds().getFourSeconds().play(player);
                }
            } else if (this.nextStorm == 60) { // Play 3 second sound
                for (GamerSession session : game.getPlayingSessions()) {
                    Player player = session.getBukkitPlayer();
                    game.getProperties().getSounds().getThreeSeconds().play(player);
                }
            } else if (this.nextStorm == 40) {// Play 2 second sound
                for (GamerSession session : game.getPlayingSessions()) {
                    Player player = session.getBukkitPlayer();
                    game.getProperties().getSounds().getTwoSeconds().play(player);
                }
            } else if (this.nextStorm == 20) { // Play 1 second sound
                for (GamerSession session : game.getPlayingSessions()) {
                    Player player = session.getBukkitPlayer();
                    game.getProperties().getSounds().getOneSecond().play(player);
                }
            }
            return;
        }
        for (GamerSession session : game.getPlayingSessions()) {
            Player player = session.getBukkitPlayer();
            if (player.isDead() || player.getTicksLived() <= 200) {
                continue;
            }
            this.victims.add(player);
            if (!nextPlatform.isStandingOn(player)) {
                player.getWorld().strikeLightningEffect(player.getLocation());
                player.setHealth(0D);
            }
        }
        setNextStorm();
    }

    private int setNextStorm() {
        this.victims.clear();
        setNextPlatform();
        IntRange range = game.getProperties().getStormCountdown();
        this.nextStorm = NumberUtil.nextInt(range.getMinimumInteger(),
                                            range.getMaximumInteger()) + 120;
        game.getPlugin().getLog().finer("Next storm ticks: " + this.nextStorm);
        return this.nextStorm;
    }

    /**
     * Sets the next {@link Platform} randomly.
     *
     * @return the randomly chosen {@link Platform}
     */
    public Platform setNextPlatform() {
        Platform platform = CollectionUtil.getRandomElement(game.getProperties().getPlatforms());
        setNextPlatform(platform);
        return platform;
    }

    /**
     * Sets the next {@link Platform}.
     *
     * @param platform platform to set
     */
    public void setNextPlatform(@NotNull Platform platform) {
        this.nextPlatform = platform;
        this.game.getPlugin().getLog().fine("Next platform is %s.", platform);
    }
}
