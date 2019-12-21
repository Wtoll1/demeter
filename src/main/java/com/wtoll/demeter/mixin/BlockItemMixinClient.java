package com.wtoll.demeter.mixin;

import com.wtoll.demeter.Demeter;
import com.wtoll.demeter.api.item.DefaultTagItem;
import com.wtoll.demeter.helper.TooltipHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.CropBlock;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.state.property.IntProperty;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(BlockItem.class)
public class BlockItemMixinClient extends Item {

    public BlockItemMixinClient(Settings settings) {
        super(settings);
    }

    @Inject(method = "appendTooltip", at = @At("HEAD"))
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context, CallbackInfo callback) {
        if (this.getBlock() instanceof CropBlock) {
            CompoundTag tag = stack.getTag();
            if (tag != null) {
                if (tag.getBoolean("observed")) {
                    CompoundTag properties = stack.getSubTag("BlockStateTag");
                    if (properties != null) {
                        Demeter.CROP_PROPERTIES.forEach((property) -> {
                            Tag propTag = properties.get(property.getName());
                            if (propTag != null) {
                                String value = TooltipHelper.parseTooltipValue(propTag.toString());
                                if (!value.equals("0")) {
                                    String name = TooltipHelper.parseTooltipValue(property.getName());
                                    if (property instanceof IntProperty) {
                                        value = TooltipHelper.toRoman(Integer.parseInt(value));
                                    }
                                    tooltip.add(new LiteralText( "§7" + TooltipHelper.capitalizeTooltip(name) + " " + value));
                                }
                            }
                        });
                    }
                }
            }
        }
    }

    @Shadow
    public Block getBlock() {
        return Blocks.AIR;
    }
}
