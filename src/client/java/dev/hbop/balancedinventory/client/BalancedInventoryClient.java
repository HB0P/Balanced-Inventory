package dev.hbop.balancedinventory.client;

import dev.hbop.balancedinventory.client.config.ModConfig;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ClientModInitializer;

public class BalancedInventoryClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        AutoConfig.register(ModConfig.class, GsonConfigSerializer::new);
        ModKeyBindings.registerKeyBindings();
    }
}
