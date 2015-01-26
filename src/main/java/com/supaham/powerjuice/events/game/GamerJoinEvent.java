package com.supaham.powerjuice.events.game;

import com.supaham.powerjuice.game.Game;
import com.supaham.powerjuice.game.GamerSession;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a {@link GamerEvent} that is fired when a {@link GamerSession} joins a {@link Game}.
 */
public class GamerJoinEvent extends GamerEvent {

    private static final HandlerList handlers = new HandlerList();

    public GamerJoinEvent(@NotNull GamerSession session) {
        super(session);
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
