package com.supaham.powerjuice.events.game;

import com.supaham.powerjuice.game.GamerSession;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerMoveEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a {@link GamerEvent} that is fired when a {@link GamerSession} moves.
 */
public class GamerMoveEvent extends GamerEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private final PlayerMoveEvent moveEvent;

    public GamerMoveEvent(@NotNull GamerSession session, @NotNull PlayerMoveEvent moveEvent) {
        super(session);
        this.moveEvent = moveEvent;
    }

    public void setTo(Location to) {
        moveEvent.setTo(to);
    }

    public Location getFrom() {
        return moveEvent.getFrom();
    }

    public void setFrom(Location from) {
        moveEvent.setFrom(from);
    }

    public Location getTo() {
        return moveEvent.getTo();
    }

    public Player getPlayer() {
        return moveEvent.getPlayer();
    }

    @Override
    public boolean isCancelled() {
        return moveEvent.isCancelled();
    }

    @Override
    public void setCancelled(boolean cancel) {
        moveEvent.setCancelled(cancel);
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
