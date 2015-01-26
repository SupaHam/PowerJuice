package com.supaham.powerjuice.events.game;

import com.supaham.powerjuice.game.GamerSession;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a {@link GamerSession} base event.
 */
public abstract class GamerEvent extends Event {

    private final GamerSession session;

    public GamerEvent(@NotNull final GamerSession session) {
        this.session = session;
    }

    /**
     * Gets the {@link GamerSession} involved in this event.
     *
     * @return session involved in this event
     */
    public GamerSession getGamerSession() {
        return session;
    }
}
