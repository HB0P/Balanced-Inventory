package dev.hbop.balancedinventory.client.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.hbop.balancedinventory.BalancedInventory;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public abstract class M_InGameHud {

    @Shadow protected abstract void renderHotbarItem(DrawContext context, int x, int y, RenderTickCounter tickCounter, PlayerEntity player, ItemStack stack, int seed);
    @Shadow @Nullable protected abstract PlayerEntity getCameraPlayer();
    
    // render the tools hotbar
    @Inject(
            method = "renderHotbar",
            at = @At("TAIL")
    )
    private void injected(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        PlayerEntity player = this.getCameraPlayer();
        assert player != null;
        
        // left and right hotbar
        RenderSystem.enableBlend();
        context.drawGuiTexture(RenderLayer::getGuiTextured, BalancedInventory.identifier("hud/hotbar_extension"), context.getScaledWindowWidth() / 2 - 157, context.getScaledWindowHeight() - 22, 62, 22);
        context.drawGuiTexture(RenderLayer::getGuiTextured, BalancedInventory.identifier("hud/hotbar_extension"), context.getScaledWindowWidth() / 2 + 95, context.getScaledWindowHeight() - 22, 62, 22);
        
        // selected slot
        int selectedSlot = player.getInventory().selectedSlot;
        if (selectedSlot >= 41 && selectedSlot <= 46) {
            int x;
            if (selectedSlot <= 43) {
                x = context.getScaledWindowWidth() / 2 - 158 + (selectedSlot - 41) * 20;
            }
            else {
                x = context.getScaledWindowWidth() / 2 + 94 + (selectedSlot - 44) * 20;
            }
            context.drawGuiTexture(RenderLayer::getGuiTextured, Identifier.ofVanilla("hud/hotbar_selection"), x, context.getScaledWindowHeight() - 23, 24, 23);
        }
        
        // items
        RenderSystem.disableBlend();
        for (int i = 0; i < 6; i++) {
            ItemStack stack = player.getInventory().getStack(i + 41);
            int x = context.getScaledWindowWidth() / 2 + (i < 3 ? -154 : 98) + (i % 3 * 20);
            renderHotbarItem(context, x, context.getScaledWindowHeight() - 19, tickCounter, this.getCameraPlayer(), stack, 1);
        }
    }
}
