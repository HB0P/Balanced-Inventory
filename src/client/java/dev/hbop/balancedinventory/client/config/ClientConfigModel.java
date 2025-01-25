package dev.hbop.balancedinventory.client.config;

import dev.hbop.balancedinventory.BalancedInventory;
import io.wispforest.owo.config.annotation.Config;
import io.wispforest.owo.config.annotation.Modmenu;
import io.wispforest.owo.config.annotation.RangeConstraint;

@Modmenu(modId = BalancedInventory.MOD_ID)
@Config(name = BalancedInventory.MOD_ID + "-client", wrapperName = "ClientConfig")
public class ClientConfigModel {
    
    public boolean showToolHotbar = true;
    public boolean scrollToToolHotbar = true;
    public boolean autoSelectTools = false;
    public boolean autoReturnOnUse = false;
    public boolean autoReturnAfterCooldown = false;
    @RangeConstraint(min = 1, max = 1200)
    public int autoReturnCooldown = 40;
}
