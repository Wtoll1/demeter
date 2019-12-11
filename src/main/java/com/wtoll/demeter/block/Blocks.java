package com.wtoll.demeter.block;

import com.wtoll.demeter.Utility;
import net.minecraft.block.Block;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.registry.Registry;

public class Blocks {
    public static Block OBSERVATION_TABLE;

    static {
        OBSERVATION_TABLE = register("observation_table", new ObservationTableBlock(), new Item.Settings().group(ItemGroup.DECORATIONS));
    }

    public static void initialize() {
    }

    public static Block register(String s, Block b, Item.Settings settings) {
        Registry.register(Registry.ITEM, Utility.id(s), new BlockItem(b, settings));
        return Registry.register(Registry.BLOCK, Utility.id(s), b);
    }


}
