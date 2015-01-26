package com.supaham.powerjuice.events.game;

import com.supaham.powerjuice.game.Game;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a {@link Game} base event.
 */
public abstract class GameEvent extends Event {

    private final Game game;

    public GameEvent(@NotNull final Game game) {
        this.game = game;
    }

    /**
     * Gets the {@link Game} involved in this event.
     *
     * @return game involved in this event
     */
    public Game getGame() {
        return game;
    }
}
