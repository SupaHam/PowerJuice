package com.supaham.powerjuice.events.game.gamersession;

import com.supaham.powerjuice.events.game.GamerEvent;
import com.supaham.powerjuice.game.GamerSession;
import com.supaham.powerjuice.weapon.Weapon;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a {@link GamerEvent} that is fired when a {@link GamerSession} is given a {@link Weapon}.
 */
public class GamerGiveWeaponEvent extends GamerEvent implements Cancellable {
    
    private static final HandlerList handlers = new HandlerList();
    
    @Getter
    @Setter
    private Weapon weapon;
    
    @Getter
    @Setter
    private boolean cancelled;
    
    public GamerGiveWeaponEvent(@NotNull GamerSession session, Weapon weapon) {
        super(session);
        this.weapon = weapon;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
    
    public static HandlerList getHandlerList() {
        return handlers;
    }
}
