package dev.hbop.balancedinventory.config;

import dev.hbop.balancedinventory.BalancedInventory;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name = BalancedInventory.MOD_ID)
public class ModConfig implements ConfigData {
    
    @ConfigEntry.Gui.CollapsibleObject(startExpanded = true)
    public InventoryConfig inventory = new InventoryConfig();
    
    public static ModConfig getConfig() {
        return AutoConfig.getConfigHolder(ModConfig.class).getConfig();
    }
    
    public static class InventoryConfig {
        @ConfigEntry.Gui.Tooltip
        public boolean scrollToToolHotbar = true;
        @ConfigEntry.Gui.Tooltip
        public boolean autoSelectTools = false;
    }
}
