package com.supaham.powerjuice.util;

import java.util.EnumSet;
import java.util.Set;

import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.material.TrapDoor;

public class BlockUtil {
    private static Set<Material> interactableBlocks = EnumSet.noneOf(Material.class);

    static {
        interactableBlocks.clear();
        interactableBlocks.add(Material.DISPENSER);
        interactableBlocks.add(Material.NOTE_BLOCK);
        interactableBlocks.add(Material.BED_BLOCK);
        interactableBlocks.add(Material.TNT);
        interactableBlocks.add(Material.CHEST);
        interactableBlocks.add(Material.WORKBENCH);
        interactableBlocks.add(Material.CROPS);
        interactableBlocks.add(Material.SOIL);
        interactableBlocks.add(Material.FURNACE);
        interactableBlocks.add(Material.BURNING_FURNACE);
        interactableBlocks.add(Material.WOODEN_DOOR);
        interactableBlocks.add(Material.LEVER);
        interactableBlocks.add(Material.IRON_DOOR_BLOCK);
        interactableBlocks.add(Material.STONE_BUTTON);
        interactableBlocks.add(Material.JUKEBOX);
        interactableBlocks.add(Material.FENCE);
        interactableBlocks.add(Material.SOUL_SAND);
        interactableBlocks.add(Material.CAKE_BLOCK);
        interactableBlocks.add(Material.DIODE_BLOCK_OFF);
        interactableBlocks.add(Material.DIODE_BLOCK_ON);
        interactableBlocks.add(Material.TRAP_DOOR);
        interactableBlocks.add(Material.IRON_FENCE);
        interactableBlocks.add(Material.FENCE_GATE);
        interactableBlocks.add(Material.NETHER_FENCE);
        interactableBlocks.add(Material.ENCHANTMENT_TABLE);
        interactableBlocks.add(Material.BREWING_STAND);
        interactableBlocks.add(Material.CAULDRON);
        interactableBlocks.add(Material.ENDER_PORTAL_FRAME);
        interactableBlocks.add(Material.DRAGON_EGG);
        // TODO possibly interactable? interactableBlocks.add(Material.COCOA);
        interactableBlocks.add(Material.ENDER_CHEST);
        interactableBlocks.add(Material.COMMAND);
        interactableBlocks.add(Material.BEACON);
        interactableBlocks.add(Material.FLOWER_POT);
        interactableBlocks.add(Material.CARROT);
        interactableBlocks.add(Material.POTATO);
        interactableBlocks.add(Material.WOOD_BUTTON);
        interactableBlocks.add(Material.ANVIL);
        interactableBlocks.add(Material.TRAPPED_CHEST);
        interactableBlocks.add(Material.REDSTONE_COMPARATOR_OFF);
        interactableBlocks.add(Material.REDSTONE_COMPARATOR_ON);
        interactableBlocks.add(Material.HOPPER);
        interactableBlocks.add(Material.DROPPER);
    }

    /**
     * Checks whether a {@link Material} can be interacted with.
     *
     * @param material material to check
     * @return true if the {@code material} can be interacted with
     */
    public static boolean isInteractableBlock(Material material) {
        return material != null && material.isBlock() && interactableBlocks.contains(material);
    }

    public static boolean isContainer(Material material) {
        switch (material) {
            case BEACON:
            case BREWING_STAND:
            case BURNING_FURNACE:
            case CHEST:
            case DISPENSER:
            case DIODE:
            case DIODE_BLOCK_OFF:
            case DIODE_BLOCK_ON:
            case DRAGON_EGG:
            case DROPPER:
            case ENCHANTMENT_TABLE:
            case ENDER_CHEST:
            case FURNACE:
            case HOPPER:
            case JUKEBOX:
            case REDSTONE_COMPARATOR:
            case REDSTONE_COMPARATOR_OFF:
            case REDSTONE_COMPARATOR_ON:
            case TRAPPED_CHEST:
            case WORKBENCH:
                return true;
            default:
                return false;
        }
    }

    public static boolean isDoor(Block block) {
        return block.getType().name().contains("DOOR");
    }

    public static boolean isTopDoorBlock(Block block) {
        return (block.getData() & 0x8) == 0x8;
    }

    public static Block getBottomDoorBlock(Block block) {
        return isTopDoorBlock(block) ? block.getRelative(BlockFace.DOWN) : block;
    }

    /**
     * Checks whether a door {@link Block} is closed.
     *
     * @param block door block to check
     * @return whether the {@code block} is closed
     */
    public static boolean isDoorClosed(Block block) {
        if (!isDoor(block)) return false;

        if (block.getType() == Material.TRAP_DOOR) {
            TrapDoor trapdoor = (TrapDoor) block.getState().getData();
            return !trapdoor.isOpen();
        } else {
            return ((getBottomDoorBlock(block).getData() & 0x4) == 0);
        }
    }

    /**
     * Toggles a door's state. If the door is open it will be closed, vice versa.
     *
     * @param block block where the door is
     * @return true if the door was opened, false if the door was closed
     */
    public static boolean toggleDoor(Block block) {
        boolean closed = isDoorClosed(block);
        if (closed) {
            openDoor(block);
        } else {
            closeDoor(block);
        }
        return closed;
    }

    public static void toggleDoor(Block block, boolean open) {
        if (open) {
            openDoor(block);
        } else {
            closeDoor(block);
        }
    }

    /**
     * Opens a door {@link Block}.
     *
     * @param block door block to open
     */
    public static void openDoor(Block block) {
        if (!isDoor(block)) return;

        if (block.getType() == Material.TRAP_DOOR) {
            BlockState state = block.getState();
            TrapDoor trapdoor = (TrapDoor) state.getData();
            trapdoor.setOpen(true);
            state.update();
        } else {
            block = getBottomDoorBlock(block);
            if (isDoorClosed(block)) {
                block.setData((byte) (block.getData() | 0x4), true);
                block.getWorld().playEffect(block.getLocation(), Effect.DOOR_TOGGLE, 0);
            }
        }
    }

    /**
     * Closes a door {@link Block}.
     *
     * @param block door block to close
     */
    public static void closeDoor(Block block) {
        if (!isDoor(block)) return;

        if (block.getType() == Material.TRAP_DOOR) {
            BlockState state = block.getState();
            TrapDoor trapdoor = (TrapDoor) state.getData();
            trapdoor.setOpen(false);
            state.update();
        } else {
            block = getBottomDoorBlock(block);
            if (!isDoorClosed(block)) {
                block.setData((byte) (block.getData() & 0xb), true);
                block.getWorld().playEffect(block.getLocation(), Effect.DOOR_TOGGLE, 0);
            }
        }
    }
}
