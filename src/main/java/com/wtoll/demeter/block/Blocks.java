package com.wtoll.demeter.block;

import com.wtoll.demeter.Utility;
import net.fabricmc.fabric.api.tag.TagRegistry;
import net.minecraft.block.Block;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.tag.Tag;
import net.minecraft.util.registry.Registry;

import static com.wtoll.demeter.Utility.id;

public class Blocks {
    public static Block OBSERVATION_TABLE;
    public static Tag<Block> FARMLAND_TAG;
    static {
        OBSERVATION_TABLE = register("observation_table", new ObservationTableBlock(), new Item.Settings().group(ItemGroup.DECORATIONS));
        FARMLAND_TAG = TagRegistry.block(id("farmland"));
    }

    public static void initialize() {
    }

    public static Block register(String s, Block b, Item.Settings settings) {
        Registry.register(Registry.ITEM, id(s), new BlockItem(b, settings));
        return Registry.register(Registry.BLOCK, id(s), b);
    }


}
