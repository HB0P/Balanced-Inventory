package dev.hbop.balancedinventory.config;

import dev.hbop.balancedinventory.BalancedInventory;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;

@Config(name = BalancedInventory.MOD_ID)
public class MainConfig implements ConfigData {
    
    public boolean restrictExtendedInventoryToEquipment = false;

    public static MainConfig getConfig() {
        return AutoConfig.getConfigHolder(MainConfig.class).getConfig();
    }
}