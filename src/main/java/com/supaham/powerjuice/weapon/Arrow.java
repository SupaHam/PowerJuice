package com.supaham.powerjuice.weapon;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class Arrow extends Weapon {

    public static final String NAME = "arrow";
    
    /**
     * {@inheritDoc}
     */
    public Arrow(@NotNull WeaponManager weaponManager) {
        super(weaponManager, NAME, "Arrow", 
              "", 
              "", 
              new ItemStack(Material.ARROW), 35);
    }

    @Override
    public void onUserAdd(Player player) {
    }

    @Override
    public void onUserRemove(Player player) {
    }
}
