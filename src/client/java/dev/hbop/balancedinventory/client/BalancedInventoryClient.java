package dev.hbop.balancedinventory.client;

import net.fabricmc.api.ClientModInitializer;

public class BalancedInventoryClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ModKeyBindings.registerKeyBindings();
    }
}
