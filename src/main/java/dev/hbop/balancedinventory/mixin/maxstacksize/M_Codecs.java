package dev.hbop.balancedinventory.mixin.maxstacksize;

import net.minecraft.util.dynamic.Codecs;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(Codecs.class)
public abstract class M_Codecs {
    
    // increase hardcoded max stack size
    // when updating - check that no unintended calls give a max of 99
    @ModifyVariable(
            method = "rangedInt(II)Lcom/mojang/serialization/Codec;",
            at = @At("HEAD"),
            ordinal = 1,
            argsOnly = true
    )
    private static int rangedInt(int value) {
        return value == 99 ? 256 : value;
    }
}
