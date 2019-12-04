package com.wtoll.demeter.api.item;

import com.wtoll.demeter.Demeter;
import com.wtoll.demeter.api.SeedDispenserBehavior;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.DispenserBlock;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.AliasedBlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.state.property.Property;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.world.World;

import java.util.Iterator;
import java.util.List;

public class SeedItem extends AliasedBlockItem implements DefaultTagItem {
    public SeedItem(Block block, Settings settings) {
        super(block, settings);
        DispenserBlock.registerBehavior(this, new SeedDispenserBehavior());
    }

    @Environment(EnvType.CLIENT)
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
        CompoundTag tag = stack.getTag();
        if (tag != null) {
            if (tag.getBoolean("observed")) {
                CompoundTag properties = stack.getSubTag("BlockStateTag");
                if (properties != null) {
                    Demeter.CROP_PROPERTIES.forEach((property) -> {
                        tooltip.add(new LiteralText(capitalize(property.getName()) + " " + parseStrings(properties.get(property.getName()).toString())));
                    });
                }
            }
        }
    }

    private String capitalize(String s) {
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }

    @Override
    public CompoundTag getDefaultTag(CompoundTag tag) {
        CompoundTag blockStateTag = tag.getCompound("BlockStateTag");
        if (blockStateTag == null) {
            blockStateTag = new CompoundTag();
        }
        Iterator<Property> i = Demeter.CROP_PROPERTIES.iterator();
        while (i.hasNext()) {
            Property property = i.next();
            blockStateTag.putString(property.getName(), property.getValues().toArray()[0].toString());
        }
        tag.put("BlockStateTag", blockStateTag);
        return tag;
    }

    private String parseStrings(String s) {
        String r = s;
        if (r.charAt(0) == '"') {
            r = r.substring(1);
        }
        if (r.charAt(r.length()-1) == '"') {
            r = r.substring(0, r.length()-1);
        }
        return r;
    }
}
