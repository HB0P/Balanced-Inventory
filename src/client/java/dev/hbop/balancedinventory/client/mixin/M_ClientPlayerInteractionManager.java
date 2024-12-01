package dev.hbop.balancedinventory.client.mixin;

import dev.hbop.balancedinventory.client.BalancedInventoryClient;
import dev.hbop.balancedinventory.client.ClientSlotData;
import dev.hbop.balancedinventory.helper.InventoryHelper;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerInteractionManager.class)
public abstract class M_ClientPlayerInteractionManager {

    @Shadow @Final private MinecraftClient client;

    @Shadow protected abstract void syncSelectedSlot();
    
    // automatically select tool when mining
    @Inject(
            method = "attackBlock",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;sendSequencedPacket(Lnet/minecraft/client/world/ClientWorld;Lnet/minecraft/client/network/SequencedPacketCreator;)V", 
                    ordinal = 1
            )
    )
    private void startAttackBlock(BlockPos pos, Direction direction, CallbackInfoReturnable<Boolean> cir) {
        
    }
    
    // don't decrement slot reset cooldown when continuing to mine
    @Inject(
            method = "attackBlock",
            at = @At("HEAD")
    )
    private void continueAttackBlock(BlockPos pos, Direction direction, CallbackInfoReturnable<Boolean> cir) {
        ClientSlotData.boostSelectedSlotResetCooldown();
    }
    
    // return to previous selected slot when using a block
    @Inject(
            method = "interactBlock",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;sendSequencedPacket(Lnet/minecraft/client/world/ClientWorld;Lnet/minecraft/client/network/SequencedPacketCreator;)V"
            )
    )
    private void interactBlock(ClientPlayerEntity player, Hand hand, BlockHitResult hitResult, CallbackInfoReturnable<ActionResult> cir) {
        if (!BalancedInventoryClient.CONFIG.autoReturnOnUse()) return;
        
        if (ClientSlotData.hasPreviouslySelectedSlot()) {
            player.getInventory().selectedSlot = ClientSlotData.getPreviouslySelectedSlot();
            ClientSlotData.reset();
            syncSelectedSlot();
        }
    }

    // return to previous selected slot when using an item
    @Inject(
            method = "interactItem",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;sendSequencedPacket(Lnet/minecraft/client/world/ClientWorld;Lnet/minecraft/client/network/SequencedPacketCreator;)V"
            )
    )
    private void interactItem(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        if (!BalancedInventoryClient.CONFIG.autoReturnOnUse()) return;

        if (ClientSlotData.hasPreviouslySelectedSlot()) {
            player.getInventory().selectedSlot = ClientSlotData.getPreviouslySelectedSlot();
            ClientSlotData.reset();
            syncSelectedSlot();
        }
    }
    
    // decrement selected slot reset cooldown every tick
    @Inject(
            method = "tick",
            at = @At("HEAD")
    )
    private void tick(CallbackInfo ci) {
        ClientSlotData.decrementSelectedSlotResetCooldown();
        if (ClientSlotData.isSelectedSlotResetCooldownElapsed()) {
            assert this.client.player != null;
            this.client.player.getInventory().selectedSlot = ClientSlotData.getPreviouslySelectedSlot();
            ClientSlotData.reset();
        }
    }
}