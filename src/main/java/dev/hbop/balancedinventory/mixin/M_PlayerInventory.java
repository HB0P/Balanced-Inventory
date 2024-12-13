package dev.hbop.balancedinventory.mixin;

import com.google.common.collect.ImmutableList;
import dev.hbop.balancedinventory.config.MainConfig;
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
    @Shadow protected abstract boolean canStackAddMore(ItemStack existingStack, ItemStack stack);

    @Shadow public abstract void setStack(int slot, ItemStack stack);

    @Shadow public int selectedSlot;

    @Unique
    private DefaultedList<ItemStack> getExtendedInventory() {
        return combinedInventory.get(3);
    }
    
    // write tool slots to nbt
    @Inject(
            method = "writeNbt",
            at = @At("RETURN")
    )
    private void writeNbt(NbtList nbtList, CallbackInfoReturnable<NbtList> cir) {
        for (int i = 0; i < this.getExtendedInventory().size(); i++) {
            if (!this.getExtendedInventory().get(i).isEmpty()) {
                NbtCompound nbtCompound = new NbtCompound();
                nbtCompound.putByte("Slot", (byte)(i + 50));
                nbtList.add(this.getExtendedInventory().get(i).toNbt(this.player.getRegistryManager(), nbtCompound));
            }
        }
    }
    
    // read tool slots from nbt
    @Inject(
            method = "readNbt",
            at = @At("RETURN")
    )
    private void readNbt(NbtList nbtList, CallbackInfo ci) {
        this.getExtendedInventory().clear();

        for (int i = 0; i < nbtList.size(); i++) {
            NbtCompound nbtCompound = nbtList.getCompound(i);
            int j = nbtCompound.getByte("Slot") & 255;
            ItemStack itemStack = ItemStack.fromNbt(this.player.getRegistryManager(), nbtCompound).orElse(ItemStack.EMPTY);
            if (j >= 50 && j < this.getExtendedInventory().size() + 50) {
                this.getExtendedInventory().set(j - 50, itemStack);
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
        cir.setReturnValue(cir.getReturnValue() + this.getExtendedInventory().size());
    }
    
    // include tool slots in empty calculation
    @Inject(
            method = "isEmpty",
            at = @At("RETURN"),
            cancellable = true
    )
    private void isEmpty(CallbackInfoReturnable<Boolean> cir) {
        for (ItemStack itemStack : this.getExtendedInventory()) {
            if (!itemStack.isEmpty()) {
                cir.setReturnValue(false);
            }
        }
    }
    
    // include extended inventory in empty slots (only if not restricted to equipment)
    @Inject(
            method = "getEmptySlot",
            at = @At("RETURN"),
            cancellable = true
    )
    private void getEmptySlot(CallbackInfoReturnable<Integer> cir) {
        if (cir.getReturnValue() == -1 && !MainConfig.getConfig().restrictExtendedInventoryToEquipment) {
            for (int i = 47; i < 41 + this.getExtendedInventory().size(); i++) {
                if (this.getStack(i).isEmpty()) {
                    cir.setReturnValue(i);
                    return;
                }
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
    
    // allow picking up items into extended inventory
    @Inject(
            method = "getOccupiedSlotWithRoomForStack",
            at = @At("RETURN"),
            cancellable = true
    )
    private void getOccupiedSlotWithRoomForStack(ItemStack stack, CallbackInfoReturnable<Integer> cir) {
        if (cir.getReturnValue() == -1) {
            for (int i = 47; i < 41 + this.getExtendedInventory().size(); i++) {
                if (this.canStackAddMore(this.getStack(i), stack)) {
                    cir.setReturnValue(i);
                    return;
                }
            }
        }
    }

    // allow pick-block from extended inventory
    @Inject(
            method = "getSlotWithStack",
            at = @At("RETURN"),
            cancellable = true
    )
    private void getSlotWithStack(ItemStack stack, CallbackInfoReturnable<Integer> cir) {
        if (cir.getReturnValue() == -1) {
            for (int i = 47; i < 41 + this.getExtendedInventory().size(); i++) {
                if (!this.getStack(i).isEmpty() && ItemStack.areItemsAndComponentsEqual(stack, this.getStack(i))) {
                    cir.setReturnValue(i);
                    return;
                }
            }
        }
    }
    
    // fix to allow inserting items into extended inventory
    @Redirect(
            method = "insertStack(ILnet/minecraft/item/ItemStack;)Z",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/collection/DefaultedList;set(ILjava/lang/Object;)Ljava/lang/Object;"
            )
    )
    private Object insertStackSet(DefaultedList<?> instance, int index, Object element) {
        this.setStack(index, (ItemStack) element);
        return null;
    }

    // fix to allow inserting items into extended inventory
    @Redirect(
            method = "insertStack(ILnet/minecraft/item/ItemStack;)Z",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/collection/DefaultedList;get(I)Ljava/lang/Object;"
            )
    )
    private Object insertStackGet(DefaultedList<?> instance, int index) {
        return this.getStack(index);
    }

    // fix to allow pick-block from extended inventory
    @Redirect(
            method = "swapSlotWithHotbar",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/collection/DefaultedList;set(ILjava/lang/Object;)Ljava/lang/Object;",
                    ordinal = 1
            )
    )
    private Object swapSlotWithHotbarSet(DefaultedList<?> instance, int index, Object element) {
        this.setStack(index, (ItemStack) element);
        return null;
    }

    // fix to allow pick-block from extended inventory
    @Redirect(
            method = "swapSlotWithHotbar",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/collection/DefaultedList;get(I)Ljava/lang/Object;",
                    ordinal = 1
            )
    )
    private Object swapSlotWithHotbarGet(DefaultedList<?> instance, int index) {
        return this.getStack(index);
    }

    // fix to prevent adding creative pick block into extended inventory
    @Redirect(
            method = "addPickBlock",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/collection/DefaultedList;set(ILjava/lang/Object;)Ljava/lang/Object;",
                    ordinal = 0
            )
    )
    private Object addPickBlock(DefaultedList<?> instance, int index, Object element) {
        if (index < this.main.size()) {
            return this.main.set(index, this.main.get(this.selectedSlot));
        }
        return null;
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
