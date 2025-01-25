package dev.hbop.balancedinventory.config;

import dev.hbop.balancedinventory.BalancedInventory;
import io.wispforest.owo.config.Option;
import io.wispforest.owo.config.annotation.Config;
import io.wispforest.owo.config.annotation.Sync;

@Config(name = BalancedInventory.MOD_ID, wrapperName = "MainConfig")
public class MainConfigModel {
    
    @Sync(Option.SyncMode.OVERRIDE_CLIENT)
    public boolean restrictExtendedInventoryToEquipment = true;
}