package com.wtoll.demeter.item;

import com.wtoll.demeter.Properties;
import net.minecraft.block.BlockState;
import net.minecraft.block.FarmlandBlock;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;

public class FertilizerItem extends Item {

    public FertilizerItem() {
        super(new Item.Settings().group(ItemGroup.MATERIALS));
    }

    public ActionResult useOnBlock(ItemUsageContext context) {
        BlockState state = context.getWorld().getBlockState(context.getBlockPos());
        ItemStack stack = context.getStack();
        if (state.getBlock() instanceof FarmlandBlock) {
            if (!state.get(Properties.FERTILIZED)) {
                context.getWorld().setBlockState(context.getBlockPos(), state.with(Properties.FERTILIZED, true));
                stack.setCount(stack.getCount() - 1);
                return ActionResult.SUCCESS;
            }
        }
        return ActionResult.PASS;
    }
}
