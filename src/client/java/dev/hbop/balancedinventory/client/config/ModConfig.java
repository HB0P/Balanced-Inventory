package dev.hbop.balancedinventory.client.config;

import dev.hbop.balancedinventory.BalancedInventory;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name = BalancedInventory.MOD_ID)
public class ModConfig implements ConfigData {

    @ConfigEntry.Gui.Tooltip
    public boolean showToolHotbar = true;
    @ConfigEntry.Gui.Tooltip
    public boolean scrollToToolHotbar = true;
    @ConfigEntry.Gui.Tooltip
    public boolean autoSelectTools = false;
    @ConfigEntry.Gui.Tooltip
    public boolean autoReturnOnUse = false;
    @ConfigEntry.Gui.Tooltip
    public boolean autoReturnAfterCooldown = false;
    @ConfigEntry.Gui.Tooltip
    public int autoReturnCooldown = 40;
    
    public static ModConfig getConfig() {
        return AutoConfig.getConfigHolder(ModConfig.class).getConfig();
    }
}
