package com.wtoll.demeter.client;

import com.wtoll.demeter.Demeter;
import com.wtoll.demeter.api.IntCropProperty;
import com.wtoll.demeter.util.TooltipHelper;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.event.client.ItemTooltipCallback;
import net.minecraft.block.CropBlock;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

/**
 * @author <Wtoll> Will Toll on 2020-05-29
 * @project Demeter
 */
@SuppressWarnings("unused")
public class DemeterClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ItemTooltipCallback.EVENT.register((stack, tooltipContext, tooltip) -> {
            Item item = stack.getItem();
            if (item instanceof BlockItem) {
                if (((BlockItem) item).getBlock() instanceof CropBlock) {
                    CompoundTag tag = stack.getTag();
                    if (tag != null) {
                        CompoundTag properties = stack.getSubTag("BlockStateTag");
                        if (properties != null) {
                            Demeter.CROP_PROPERTIES.forEach((property) -> {
                                Tag propTag = properties.get(property.getName());
                                if (propTag != null) {
                                    String value = TooltipHelper.parseTooltipValue(propTag.toString());
                                    if (!value.equals("0")) {
                                        String name = TooltipHelper.parseTooltipValue(property.getName());
                                        if (property instanceof IntCropProperty) {
                                            value = TooltipHelper.toRoman(Integer.parseInt(value));
                                        }
                                        tooltip.add(1, new TranslatableText(property.getTextPath()).append(new LiteralText(" " + value)).setStyle(Style.EMPTY.withColor(Formatting.GRAY)));
                                    }
                                }
                            });
                        }
                    }
                }
            }
        });
    }
}
