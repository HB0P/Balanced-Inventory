package dev.hbop.balancedinventory.mixin.maxstacksize;

import dev.hbop.balancedinventory.helper.StackSizes;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Function;

@Mixin(Items.class)
public abstract class M_Items {

    // change max stack sizes
    @Inject(
            method = "register(Lnet/minecraft/registry/RegistryKey;Ljava/util/function/Function;Lnet/minecraft/item/Item$Settings;)Lnet/minecraft/item/Item;",
            at = @At("HEAD")
    )
    private static void register(RegistryKey<Item> key, Function<Item.Settings, Item> factory, Item.Settings settings, CallbackInfoReturnable<Item> cir) {
        if (StackSizes.STACK_SIZES.containsKey(key.getValue())) {
            settings.maxCount(StackSizes.STACK_SIZES.get(key.getValue()));
        }
    }
}
