package com.supaham.powerjuice.util;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

public class MaterialUtil {

    public static boolean same(Block block, MaterialData materialData) {
        return same(block.getType(), block.getData(), materialData);
    }

    public static boolean same(ItemStack item, MaterialData materialData) {
        return same(item.getData(), materialData);
    }

    public static boolean same(Material type, byte data, MaterialData materialData) {
        return same(new MaterialData(type, data), materialData);
    }

    public static boolean same(MaterialData o1, MaterialData o2) {
        if (o1 == null) {
            return o2 == null;
        } else if (o2 == null) {
            return false;
        }
        return o1.getItemType().equals(o2.getItemType())
               && ((o1.getData() == -1 || o2.getData() == -1)
               || o1.getData() == o2.getData());
    }
}
