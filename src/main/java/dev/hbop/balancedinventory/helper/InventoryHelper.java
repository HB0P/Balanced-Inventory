package dev.hbop.balancedinventory.helper;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class InventoryHelper {
    
    /**
     * Get the first slot in the hotbar matching a predicate<br>
     * The tool hotbar is tested first (left to right), followed by the main hotbar (left to right)
     * @param inventory The inventory to test in
     * @param predicate The predicate to test stacks against
     * @return The first matching slot, or -1 if none are found
     */
    public static int getSlotInHotbarMatching(PlayerInventory inventory, Predicate<ItemStack> predicate) {
        for (int i = 41; i <= 46; i++) {
            ItemStack stack = inventory.getStack(i);
            if (predicate.test(stack)) {
                return i;
            }
        }
        for (int i = 0; i <= 8; i++) {
            ItemStack stack = inventory.getStack(i);
            if (predicate.test(stack)) {
                return i;
            }
        }
        return -1;
    }
    
    public static void addExtraSlots(PlayerInventory inventory, int width, int height, Consumer<Slot> consumer) {
        // left hotbar
        for (int i = 0; i < 3; i++) {
            consumer.accept(new EquipmentSlot(inventory, i + 41, -50 + i * 18, height - 24));
        }
        // right hotbar
        for (int i = 0; i < 3; i++) {
            consumer.accept(new EquipmentSlot(inventory, i + 44, width - 2 + i * 18, height - 24));
        }
        // left inventory
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 3; x++) {
                consumer.accept(new EquipmentSlot(inventory, y * 3 + x + 47, -50 + x * 18, height - 82 + y * 18));
            }
        }
        // right inventory
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 3; x++) {
                consumer.accept(new EquipmentSlot(inventory, y * 3 + x + 56, width - 2 + x * 18, height - 82 + y * 18));
            }
        }
    }
    
    public static void addExtraSlots(PlayerInventory inventory, Consumer<Slot> consumer) {
        addExtraSlots(inventory, 176, 166, consumer);
    }
    
    private static class EquipmentSlot extends Slot {
        private EquipmentSlot(Inventory inventory, int index, int x, int y) {
            super(inventory, index, x, y);
        }
        
        @Override
        public boolean canInsert(ItemStack stack) {
            return stack.isDamageable();
        }
    }
}
