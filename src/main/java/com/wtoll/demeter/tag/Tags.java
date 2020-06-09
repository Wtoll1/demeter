package com.wtoll.demeter.tag;

import com.wtoll.demeter.Demeter;
import net.fabricmc.fabric.api.tag.TagRegistry;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tag.Tag;

/**
 * @author <Wtoll> Will Toll on 2020-05-28
 * @project Demeter
 */
public class Tags {
    public static final Tag<Item> SEED = TagRegistry.item(Demeter.id("seeds"));

    public static final Tag<Block> SPREADABLE = TagRegistry.block(Demeter.id("spreadable"));
    public static final Tag<Block> CROP = TagRegistry.block(Demeter.id("crops"));

    public static void init() {

    }
}
