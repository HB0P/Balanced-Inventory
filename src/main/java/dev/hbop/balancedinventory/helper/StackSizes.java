package dev.hbop.balancedinventory.helper;

import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

public class StackSizes {

    public static final Map<Identifier, Integer> STACK_SIZES = new HashMap<>();
    private static final String[] STACKABLE_256 = new String[] {
            "cobblestone", "stone", "cobbled_deepslate", "deepslate", "dirt", "sand", "gravel",
            "oak_planks", "spruce_planks", "birch_planks", "jungle_planks", "acacia_planks", "dark_oak_planks", "mangrove_planks", "cherry_planks", "pale_oak_planks", "bamboo_planks", "crimson_planks", "warped_planks",
            "oak_log", "spruce_log", "birch_log", "jungle_log", "acacia_log", "dark_oak_log", "mangrove_log", "cherry_log", "pale_oak_log", "bamboo_block", "crimson_stem", "warped_stem"
    };
    private static final String[] STACKABLE_64 = new String[] {
            
    };
    private static final String[] STACKABLE_16 = new String[] {
            "potion", "mushroom_stew", "rabbit_stew", "beetroot_soup"
    };
    static {
        for (String string : STACKABLE_256) {
            STACK_SIZES.put(Identifier.ofVanilla(string), 256);
        }
        for (String string : STACKABLE_64) {
            STACK_SIZES.put(Identifier.ofVanilla(string), 64);
        }
        for (String string : STACKABLE_16) {
            STACK_SIZES.put(Identifier.ofVanilla(string), 16);
        }
    }
}
