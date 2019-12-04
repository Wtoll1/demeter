package com.wtoll.demeter.item;

import com.wtoll.demeter.Utility;
import net.minecraft.item.Item;
import net.minecraft.util.registry.Registry;

public class Items {
    public static final Item FERTILIZER;

    static {
        FERTILIZER = register("fertilizer", new FertilizerItem());
    }

    public static void initialize() {  }

    public static Item register(String s, Item i) {
        return Registry.register(Registry.ITEM, Utility.id(s), i);
    }
}
