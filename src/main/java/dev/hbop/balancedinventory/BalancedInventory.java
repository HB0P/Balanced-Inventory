package dev.hbop.balancedinventory;

import dev.hbop.balancedinventory.config.ModConfig;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BalancedInventory implements ModInitializer {

    public static final String MOD_ID = "balancedinventory";
    @SuppressWarnings("unused")
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    
    @Override
    public void onInitialize() {
        AutoConfig.register(ModConfig.class, GsonConfigSerializer::new);
    }

    public static Identifier identifier(String id) {
        return Identifier.of(MOD_ID, id);
    }

    @SuppressWarnings("unused")
    public static void log(Object object) {
        if (object == null) LOGGER.info(null);
        else LOGGER.info(object.toString());
    }
}
