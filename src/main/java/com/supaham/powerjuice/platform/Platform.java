package com.supaham.powerjuice.platform;

import com.supaham.powerjuice.util.MaterialUtil;
import lombok.Getter;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Created by Ali on 30/08/2014.
 */
public class Platform {

    @Getter
    private final PlatformProperties properties;

    public Platform(@NotNull PlatformProperties properties) {
        this.properties = properties;
    }

    /**
     * Checks whether a {@link Player} is standing on this {@link Platform}.
     *
     * @param player player to check
     * @return whether the {@code player} is standing on this {@link Platform}
     */
    public boolean isStandingOn(Player player) {
        Block under = player.getLocation().getBlock().getRelative(BlockFace.DOWN);
        return MaterialUtil.same(under, properties.getMaterialData());
    }

    @Override
    public String toString() {
        return this.properties.toString();
    }
}
