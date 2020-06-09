package com.wtoll.demeter.mixin;

import com.wtoll.demeter.Demeter;
import com.wtoll.demeter.api.IntCropProperty;
import com.wtoll.demeter.tag.Tags;
import com.wtoll.demeter.util.MathUtil;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.CropBlock;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * @author <Wtoll> Will Toll on 2020-05-29
 * @project Demeter
 */
@Mixin(AbstractBlock.class)
public class AbstractBlockMixin {
    @Shadow
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {  }

    @Inject(method = "scheduledTick", at = @At("HEAD"))
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random, CallbackInfo callback) {
        if (state.getBlock() instanceof CropBlock && Tags.CROP.contains(state.getBlock())) {
            int growth = (int) Demeter.GROWTH.get(world.getBlockState(pos));
            if (random.nextFloat() < MathUtil.map(growth, ((IntCropProperty) Demeter.GROWTH).min(), ((IntCropProperty) Demeter.GROWTH).max(), 0f, Demeter.config.getMaxGrowthProbability())) {
                this.randomTick(state, world, pos, random);
            }
            this.reschedule(world, pos);
        }
    }

    @Inject(method = "onBlockAdded", at = @At("HEAD"))
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean moved, CallbackInfo callback) {
        if (state.getBlock() instanceof CropBlock && Tags.CROP.contains(state.getBlock())) {
            this.reschedule(world, pos);
        }
    }

    @Inject(method = "getDroppedStacks", at = @At(value = "RETURN", ordinal = 1), cancellable = true)
    public void getDroppedStacks(BlockState state, LootContext.Builder builder, CallbackInfoReturnable<List<ItemStack>> callback) {
        if (state.getBlock() instanceof CropBlock && Tags.CROP.contains(state.getBlock())) {
            List<ItemStack> response = callback.getReturnValue();
            response.forEach((stack) -> {
                Item item = stack.getItem();
                if (item instanceof BlockItem && ((BlockItem) item).getBlock().equals(this) && Tags.SEED.contains(item)) {
                    CompoundTag tag = stack.getOrCreateSubTag("BlockStateTag");
                    Demeter.CROP_PROPERTIES.forEach((property) -> tag.putString(property.getName(), state.get(property.blockProperty()).toString()));
                    stack.putSubTag("BlockStateTag", tag);
                }
            });
            callback.setReturnValue(response);
        }
    }

    private void reschedule(World world, BlockPos pos) {
        world.getBlockTickScheduler().schedule(pos, world.getBlockState(pos).getBlock(), 20);
    }
}
