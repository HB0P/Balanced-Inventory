package dev.hbop.balancedinventory.client;

import dev.hbop.balancedinventory.client.config.ClientConfig;
import net.fabricmc.api.ClientModInitializer;

public class BalancedInventoryClient implements ClientModInitializer {

    public static final ClientConfig CONFIG = ClientConfig.createAndLoad();
    
    @Override
    public void onInitializeClient() {
        ModKeyBindings.registerKeyBindings();
    }
}
