package com.supaham.powerjuice.game;

import com.supaham.powerjuice.events.game.GameStopEvent.Reason;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a {@link BukkitRunnable} that is called for every tick of the {@link Game}.
 */
public class GameTick extends BukkitRunnable {

    private final Game game;
    private GameProperties properties;
    private GameScoreboard scoreboard;

    private int ticksToSecond = 0;
    private boolean secondTick = false;
    
    protected GameTick(@NotNull Game game) {
        this.game = game;
    }

    protected void init() {
        this.properties = game.getProperties();
        this.scoreboard = game.getScoreboard();
    }

    protected void start() {
        runTaskTimer(game.getPlugin(), 0, 1);
    }

    @Override
    public void run() {
        if (++ticksToSecond == 20) {
            secondTick = true;
            ticksToSecond = 0;
        }

        if (secondTick) {
            if (properties.getEndTime() <= System.currentTimeMillis()) {
                game.getManager().stop(Reason.OVERTIME);
                return;
            }
        }
        game.getBossBar().tick();
        game.getStorm().tick(secondTick);

        game.getGamerSessions().forEach(t -> t.tick(secondTick));
        secondTick = false;
    }
}
