package dev.hbop.balancedinventory.client;

import dev.hbop.balancedinventory.client.config.ModConfig;

public class ClientSlotData {
    
    private static int previousSelectedSlot = -1;
    private static int selectedSlotResetCooldown = -1;
    
    public static boolean hasPreviouslySelectedSlot() {
        return previousSelectedSlot >= 0;
    }
    
    public static int getPreviouslySelectedSlot() {
        return previousSelectedSlot;
    }
    
    public static boolean isSelectedSlotResetCooldownElapsed() {
        return selectedSlotResetCooldown == 0;
    }
    
    public static void reset() {
        previousSelectedSlot = -1;
        selectedSlotResetCooldown = -1;
    }
    
    public static void set(int selectedSlot, boolean returnAfterCooldown) {
        if (selectedSlot <= 8) {
            ClientSlotData.previousSelectedSlot = selectedSlot;
            if (returnAfterCooldown && ModConfig.getConfig().autoReturnAfterCooldown) {
                ClientSlotData.selectedSlotResetCooldown = ModConfig.getConfig().autoReturnCooldown;
            }
        }
    }
    
    public static void decrementSelectedSlotResetCooldown() {
        if (selectedSlotResetCooldown >= 0) {
            selectedSlotResetCooldown--;
        }
    }
    
    public static void boostSelectedSlotResetCooldown() {
        if (selectedSlotResetCooldown >= 0) {
            selectedSlotResetCooldown = ModConfig.getConfig().autoReturnCooldown;
        }
    }
}