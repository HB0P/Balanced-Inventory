package dev.hbop.balancedinventory.client.mixin;

import dev.hbop.balancedinventory.client.ClientSlotData;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public abstract class M_MinecraftClient {

    @Shadow @Nullable public ClientPlayerEntity player;

    // reset previous selected slot when changing slot manually
    @Inject(
            method = "handleInputEvents",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/entity/player/PlayerInventory;selectedSlot:I",
                    opcode = Opcodes.PUTFIELD
            )
    )
    private void handleInputEvents(CallbackInfo ci) {
        ClientSlotData.reset();
    }

    // reset previous selected slot when changing slot manually
    @Inject(
            method = "doItemPick",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/entity/player/PlayerInventory;selectedSlot:I",
                    opcode = Opcodes.PUTFIELD
            )
    )
    private void doItemPick(CallbackInfo ci) {
        ClientSlotData.reset();
    }
    
    // prevent swapping hands with tool hotbar
    @Redirect(
            method = "handleInputEvents",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/option/KeyBinding;wasPressed()Z",
                    ordinal = 0
            ),
            slice = @Slice(
                    from = @At(
                            value = "FIELD",
                            target = "Lnet/minecraft/client/option/GameOptions;swapHandsKey:Lnet/minecraft/client/option/KeyBinding;"
                    )
            )
    )
    private boolean swapHands(KeyBinding instance) {
        assert this.player != null;
        if (this.player.getInventory().selectedSlot <= 8) {
            return instance.wasPressed();
        }
        instance.wasPressed();
        return false;
    }
}