package dev.hbop.balancedinventory.client;

import dev.hbop.balancedinventory.client.config.ClientConfig;
import dev.hbop.balancedinventory.helper.InventoryHelper;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.minecraft.block.BlockState;
import net.minecraft.util.ActionResult;

public class BalancedInventoryClient implements ClientModInitializer {

    public static final ClientConfig CONFIG = ClientConfig.createAndLoad();
    
    @Override
    public void onInitializeClient() {
        ModKeyBindings.registerKeyBindings();
        AttackBlockCallback.EVENT.register((player, world, hand, pos, direction) -> {
            if (!BalancedInventoryClient.CONFIG.autoSelectTools()) return ActionResult.PASS;
            
            BlockState state = world.getBlockState(pos);
            int i = InventoryHelper.getSlotInHotbarMatching(player.getInventory(), stack -> stack.isSuitableFor(state));
            if (i != -1) {
                ClientSlotData.set(player.getInventory().selectedSlot, true);
                player.getInventory().selectedSlot = i;
            }
            return ActionResult.PASS;
        });
    }
}
