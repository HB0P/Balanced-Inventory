package dev.hbop.balancedinventory.client.mixin;

import dev.hbop.balancedinventory.config.ModConfig;
import net.minecraft.client.Mouse;
import net.minecraft.client.input.Scroller;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Mouse.class)
public abstract class M_Mouse {
    
    @Redirect(
            method = "onMouseScroll",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/input/Scroller;scrollCycling(DII)I"
            )
    )
    private int scrollCycling(double amount, int selectedSlot, int hotbarSize) {
        if (!ModConfig.getConfig().inventory.scrollToToolHotbar && selectedSlot <= 8) {
            return Scroller.scrollCycling(amount, selectedSlot, hotbarSize);
        }

        int slotPosition;
        if (selectedSlot >= 0 && selectedSlot <= 8) {
            slotPosition = selectedSlot + 3;
        } else if (selectedSlot >= 41 && selectedSlot <= 43) {
            slotPosition = selectedSlot - 41;
        } else if (selectedSlot >= 44 && selectedSlot <= 46) {
            slotPosition = selectedSlot - 32;
        } else throw new RuntimeException();

        slotPosition = Scroller.scrollCycling(amount, slotPosition, 15);

        if (slotPosition >= 3 && slotPosition <= 11) {
            selectedSlot = slotPosition - 3;
        } else if (slotPosition >= 0 && slotPosition <= 2) {
            selectedSlot = slotPosition + 41;
        } else if (slotPosition >= 12) {
            selectedSlot = slotPosition + 32;
        }

        return selectedSlot;
    }
}
