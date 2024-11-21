package dev.hbop.balancedinventory.mixin;

import com.google.common.collect.ImmutableList;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.collection.DefaultedList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(PlayerInventory.class)
public abstract class M_PlayerInventory {

    @Shadow @Final public DefaultedList<ItemStack> main;
    @Shadow @Final public DefaultedList<ItemStack> armor;
    @Shadow @Final public DefaultedList<ItemStack> offHand;
    @Shadow @Final public PlayerEntity player;
    @Shadow
    private final List<DefaultedList<ItemStack>> combinedInventory = ImmutableList.of(this.main, this.armor, this.offHand, DefaultedList.ofSize(24, ItemStack.EMPTY));
    @Shadow public abstract ItemStack getStack(int slot);

    @Unique
    private DefaultedList<ItemStack> getTools() {
        return combinedInventory.get(3);
    }
    
    // write tool slots to nbt
    @Inject(
            method = "writeNbt",
            at = @At("RETURN")
    )
    private void writeNbt(NbtList nbtList, CallbackInfoReturnable<NbtList> cir) {
        for (int i = 0; i < this.getTools().size(); i++) {
            if (!this.getTools().get(i).isEmpty()) {
                NbtCompound nbtCompound = new NbtCompound();
                nbtCompound.putByte("Slot", (byte)(i + 50));
                nbtList.add(this.getTools().get(i).toNbt(this.player.getRegistryManager(), nbtCompound));
            }
        }
    }
    
    // read tool slots from nbt
    @Inject(
            method = "readNbt",
            at = @At("RETURN")
    )
    private void readNbt(NbtList nbtList, CallbackInfo ci) {
        this.getTools().clear();

        for (int i = 0; i < nbtList.size(); i++) {
            NbtCompound nbtCompound = nbtList.getCompound(i);
            int j = nbtCompound.getByte("Slot") & 255;
            ItemStack itemStack = ItemStack.fromNbt(this.player.getRegistryManager(), nbtCompound).orElse(ItemStack.EMPTY);
            if (j >= 50 && j < this.getTools().size() + 50) {
                this.getTools().set(j - 50, itemStack);
            }
        }
    }
    
    // include tool slots inventory size
    @Inject(
            method = "size",
            at = @At("RETURN"),
            cancellable = true
    )
    private void size(CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(cir.getReturnValue() + this.getTools().size());
    }
    
    // include tool slots in empty calculation
    @Inject(
            method = "isEmpty",
            at = @At("RETURN"),
            cancellable = true
    )
    private void isEmpty(CallbackInfoReturnable<Boolean> cir) {
        for (ItemStack itemStack : this.getTools()) {
            if (!itemStack.isEmpty()) {
                cir.setReturnValue(false);
            }
        }
    }
    
    // allow tool hotbar indexes
    @Inject(
            method = "isValidHotbarIndex",
            at = @At("RETURN"),
            cancellable = true
    )
    private static void isValidHotbarIndex(int slot, CallbackInfoReturnable<Boolean> cir) {
        if (slot >= 41 && slot <= 46) {
            cir.setReturnValue(true);
        }
    }
    
    // look in whole inventory for main hand stack
    @Redirect(
            method = "getMainHandStack",
            at = @At(
                    value = "INVOKE", 
                    target = "Lnet/minecraft/util/collection/DefaultedList;get(I)Ljava/lang/Object;"
            )
    )
    private Object getMainHandStack(DefaultedList<ItemStack> instance, int index) {
        return getStack(index);
    }
    
    // look in whole inventory for selected item
    @Redirect(
            method = "getBlockBreakingSpeed",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/collection/DefaultedList;get(I)Ljava/lang/Object;"
            )
    )
    private Object getBlockBreakingSpeed(DefaultedList<ItemStack> instance, int index) {
        return getStack(index);
    }
}
