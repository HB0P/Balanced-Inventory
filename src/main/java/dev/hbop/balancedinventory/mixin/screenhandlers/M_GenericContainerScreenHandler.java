package dev.hbop.balancedinventory.mixin.screenhandlers;

import dev.hbop.balancedinventory.helper.InventoryHelper;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GenericContainerScreenHandler.class)
public abstract class M_GenericContainerScreenHandler extends ScreenHandler {

    @Shadow @Final private int rows;

    protected M_GenericContainerScreenHandler(@Nullable ScreenHandlerType<?> type, int syncId) {
        super(type, syncId);
    }
    
    @Inject(
            method = "<init>(Lnet/minecraft/screen/ScreenHandlerType;ILnet/minecraft/entity/player/PlayerInventory;Lnet/minecraft/inventory/Inventory;I)V",
            at = @At("TAIL")
    )
    private void init(ScreenHandlerType<?> type, int syncId, PlayerInventory playerInventory, Inventory inventory, int rows, CallbackInfo ci) {
        InventoryHelper.addExtraSlots(playerInventory, 176, 113 + this.rows * 18, slot -> this.addSlot(slot));
    }
}
