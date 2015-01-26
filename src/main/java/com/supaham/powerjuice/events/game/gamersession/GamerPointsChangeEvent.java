package com.supaham.powerjuice.events.game.gamersession;

import com.supaham.powerjuice.events.game.GamerEvent;
import com.supaham.powerjuice.game.GamerSession;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a {@link GamerEvent} that is fired when a {@link GamerSession} has a change in balance.
 */
public class GamerPointsChangeEvent extends GamerEvent implements Cancellable {
    
    private static final HandlerList handlers = new HandlerList();
    
    @Getter
    private int oldBalance;

    @Getter
    @Setter
    private int newBalance;
    
    @Getter
    @Setter
    private boolean cancelled;
    
    public GamerPointsChangeEvent(@NotNull GamerSession session, int oldBalance, int newBalance) {
        super(session);
        this.oldBalance = oldBalance;
        this.newBalance = newBalance;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
    
    public int getDifference() {
        return this.oldBalance - this.newBalance;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
