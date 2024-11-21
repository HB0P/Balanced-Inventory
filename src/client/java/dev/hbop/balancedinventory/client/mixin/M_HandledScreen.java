package dev.hbop.balancedinventory.client.mixin;

import dev.hbop.balancedinventory.BalancedInventory;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.ShulkerBoxScreen;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HandledScreen.class)
public abstract class M_HandledScreen extends Screen {

    @Shadow protected int x;
    @Shadow protected int y;
    @Shadow protected int backgroundHeight;
    @Shadow protected int backgroundWidth;
    @Unique
    private static final Identifier EXTENSION_TEXTURE = BalancedInventory.identifier("textures/gui/container/inventory_extension.png");

    protected M_HandledScreen(Text title) {
        super(title);
    }
    
    // render tool slots area
    @Inject(
            method = "renderBackground",
            at = @At("TAIL")
    )
    private void renderBackground(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if ((Screen) this instanceof CreativeInventoryScreen) return;
        int height = this.backgroundHeight;
        if ((Screen) this instanceof ShulkerBoxScreen) height--;
        else if ((Screen) this instanceof GenericContainerScreen) height--;
        context.drawTexture(RenderLayer::getGuiTextured, EXTENSION_TEXTURE, this.x - 58, this.y + height - 90, 0, 0, 62, 90, 256, 256);
        context.drawTexture(RenderLayer::getGuiTextured, EXTENSION_TEXTURE, this.x + this.backgroundWidth - 4, this.y + height - 90, 194, 0, 62, 90, 256, 256);
    }
    
    // stop tool slots being considered "out of bounds"
    @Inject(
            method = "isClickOutsideBounds",
            at = @At("HEAD"),
            cancellable = true
    )
    private void isClickOutsideBounds(double mouseX, double mouseY, int left, int top, int button, CallbackInfoReturnable<Boolean> cir) {
        if (mouseX > (left - 58) && mouseX < left + backgroundWidth + 58 && mouseY > top + backgroundHeight - 90 && mouseY < top + backgroundHeight) {
            cir.setReturnValue(false);
        }
    }
}
