package dev.hbop.balancedinventory.mixin;

import dev.hbop.balancedinventory.config.ModConfig;
import dev.hbop.balancedinventory.helper.InventoryHelper;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractBlock.AbstractBlockState.class)
public abstract class M_AbstractBlockState {
    
    // switch selected hotbar slot when starting mining
    @Inject(
            method = "onBlockBreakStart",
            at = @At("HEAD")
    )
    private void onBlockBreakStart(World world, BlockPos pos, PlayerEntity player, CallbackInfo ci) {
        if (!ModConfig.getConfig().inventory.autoSelectTools) return;
        
        BlockState state = world.getBlockState(pos);
        int i = InventoryHelper.getSlotInHotbarMatching(player.getInventory(), stack -> stack.isSuitableFor(state));
        if (i != -1) {
            player.getInventory().selectedSlot = i;
        }
    }
}
