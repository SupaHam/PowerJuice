package com.supaham.powerjuice.events.game;

import com.supaham.powerjuice.game.Game;
import lombok.Getter;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a {@link GameEvent} that is fired when a {@link Game} is stopped.
 */
public class GameStopEvent extends GameEvent {

    private static final HandlerList handlers = new HandlerList();
    
    @Getter
    private final Reason reason;

    public GameStopEvent(@NotNull Game game, @NotNull Reason reason) {
        super(game);
        this.reason = reason;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
    
    public static enum Reason {
        GOAL_REACHED, OVERTIME, COMMAND;
    }
}
