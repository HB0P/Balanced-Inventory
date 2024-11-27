package dev.hbop.balancedinventory.client.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.hbop.balancedinventory.BalancedInventory;
import dev.hbop.balancedinventory.client.config.ModConfig;
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
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Function;

@Mixin(InGameHud.class)
public abstract class M_InGameHud {

    @Shadow protected abstract void renderHotbarItem(DrawContext context, int x, int y, RenderTickCounter tickCounter, PlayerEntity player, ItemStack stack, int seed);
    @Shadow @Nullable protected abstract PlayerEntity getCameraPlayer();
    
    // render the tools hotbar
    @Inject(
            method = "renderHotbar",
            at = @At("TAIL")
    )
    private void renderHotbar(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        if (!ModConfig.getConfig().showToolHotbar) return;
        
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
    
    // shift the offhand when showing tool hotbar
    @Redirect(
            method = "renderHotbar",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/DrawContext;drawGuiTexture(Ljava/util/function/Function;Lnet/minecraft/util/Identifier;IIII)V",
                    ordinal = 0
            ),
            slice = @Slice(
                    from = @At(
                            value = "FIELD",
                            target = "Lnet/minecraft/client/gui/hud/InGameHud;HOTBAR_OFFHAND_LEFT_TEXTURE:Lnet/minecraft/util/Identifier;"
                    )
            )
    )
    private void renderOffhandLeft(DrawContext context, Function<Identifier, RenderLayer> renderLayers, Identifier sprite, int x, int y, int width, int height) {
        if (ModConfig.getConfig().showToolHotbar) {
            context.drawGuiTexture(renderLayers, sprite, x - 66, y, width, height);
        }
        else {
            context.drawGuiTexture(renderLayers, sprite, x, y, width, height);
        }
    }

    @Redirect(
            method = "renderHotbar",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/DrawContext;drawGuiTexture(Ljava/util/function/Function;Lnet/minecraft/util/Identifier;IIII)V",
                    ordinal = 0
            ),
            slice = @Slice(
                    from = @At(
                            value = "FIELD",
                            target = "Lnet/minecraft/client/gui/hud/InGameHud;HOTBAR_OFFHAND_RIGHT_TEXTURE:Lnet/minecraft/util/Identifier;"
                    )
            )
    )
    private void renderOffhandRight(DrawContext context, Function<Identifier, RenderLayer> renderLayers, Identifier sprite, int x, int y, int width, int height) {
        if (ModConfig.getConfig().showToolHotbar) {
            context.drawGuiTexture(renderLayers, sprite, x + 66, y, width, height);
        }
        else {
            context.drawGuiTexture(renderLayers, sprite, x, y, width, height);
        }
    }
    
    @Redirect(
            method = "renderHotbar",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/hud/InGameHud;renderHotbarItem(Lnet/minecraft/client/gui/DrawContext;IILnet/minecraft/client/render/RenderTickCounter;Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/item/ItemStack;I)V",
                    ordinal = 1
            )
    )
    private void renderOffhandItemLeft(InGameHud hud, DrawContext context, int x, int y, RenderTickCounter tickCounter, PlayerEntity player, ItemStack stack, int seed) {
        if (ModConfig.getConfig().showToolHotbar) {
            renderHotbarItem(context, x - 66, y, tickCounter, player, stack, seed);
        }
        else {
            renderHotbarItem(context, x, y, tickCounter, player, stack, seed);
        }
    }

    @Redirect(
            method = "renderHotbar",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/hud/InGameHud;renderHotbarItem(Lnet/minecraft/client/gui/DrawContext;IILnet/minecraft/client/render/RenderTickCounter;Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/item/ItemStack;I)V",
                    ordinal = 2
            )
    )
    private void renderOffhandItemRight(InGameHud hud, DrawContext context, int x, int y, RenderTickCounter tickCounter, PlayerEntity player, ItemStack stack, int seed) {
        if (ModConfig.getConfig().showToolHotbar) {
            renderHotbarItem(context, x + 66, y, tickCounter, player, stack, seed);
        }
        else {
            renderHotbarItem(context, x, y, tickCounter, player, stack, seed);
        }
    }
}
