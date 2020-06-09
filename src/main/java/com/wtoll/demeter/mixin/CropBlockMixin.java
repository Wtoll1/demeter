package com.wtoll.demeter.mixin;

import com.wtoll.demeter.Demeter;
import com.wtoll.demeter.recipe.BreedingRecipe;
import com.wtoll.demeter.util.MathUtil;
import com.wtoll.demeter.tag.Tags;
import com.wtoll.demeter.api.CropProperty;
import com.wtoll.demeter.api.IntCropProperty;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CropBlock;
import net.minecraft.block.PlantBlock;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * @author <Wtoll> Will Toll on 2020-05-28
 * @project Demeter
 */
@Mixin(CropBlock.class)
public abstract class CropBlockMixin extends PlantBlock {

    private static final List<BlockPos> BREED_POSITIONS = Arrays.asList(
            new BlockPos(1, 0, 0),
            new BlockPos(-1, 0, 0),
            new BlockPos(0, 0, 1),
            new BlockPos(0, 0, -1)
    );

    protected CropBlockMixin(Settings settings) {
        super(settings);
    }

    @SuppressWarnings("deprecation")
    @Shadow
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
    }

    @Shadow
    public IntProperty getAgeProperty() {
        return IntProperty.of("", 0, 0);
    }

    @Shadow
    public int getMaxAge() {
        return 7;
    }

    @Inject(method = "randomTick", at = @At(value = "INVOKE", target="Lnet/minecraft/server/world/ServerWorld;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;I)Z", shift = At.Shift.AFTER))
    public void randomTickStateOverride(BlockState state, ServerWorld world, BlockPos pos, Random random, CallbackInfo callback) {
        if (Tags.CROP.contains(state.getBlock())) {
            world.setBlockState(pos, state.with(getAgeProperty(), state.get(getAgeProperty()) + 1), 2);
        }
    }

    @Inject(method = "randomTick", at = @At(value = "TAIL"))
    public void breedRandomTick(BlockState state, ServerWorld world, BlockPos pos, Random random, CallbackInfo callback) {
        if (Tags.CROP.contains(state.getBlock())) {
            if (state.get(this.getAgeProperty()) >= this.getMaxAge()) {
                for (BlockPos iPos : BREED_POSITIONS) {
                    BlockPos breedPos = pos.add(iPos);
                    for (BlockPos jPos : BREED_POSITIONS) {
                        if (world.isAir(breedPos)) {
                            BlockPos parent2Pos = breedPos.add(jPos);
                            if (!pos.equals(parent2Pos)) {
                                BlockState parent2State = world.getBlockState(parent2Pos);
                                if (parent2State.getBlock() instanceof CropBlock && Tags.CROP.contains(parent2State.getBlock()) && parent2State.get(((CropBlock) parent2State.getBlock()).getAgeProperty()) >= ((CropBlock) parent2State.getBlock()).getMaxAge()) {
                                    BreedingRecipe.findRecipe(world, Registry.BLOCK.getId(state.getBlock()), Registry.BLOCK.getId(parent2State.getBlock())).ifPresent((identifier) -> {
                                        if (random.nextFloat() < Demeter.config.getBreedProbability()) {
                                            Block block = Registry.BLOCK.get(identifier);
                                            BlockState breedState = block.getDefaultState();
                                            if (block instanceof CropBlock) {
                                                breedState = breedState.with(((CropBlock) block).getAgeProperty(), 0);
                                                for (CropProperty property : Demeter.CROP_PROPERTIES.getProperties()) {
                                                    breedState = property.breed(breedState, state, parent2State, random);
                                                    breedState = property.mutate(breedState, random);
                                                }
                                            }
                                            if (block.canPlaceAt(breedState, world, breedPos)) {
                                                world.setBlockState(breedPos, breedState);
                                            }
                                        }
                                    });
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Inject(method = "randomTick", at = @At(value = "TAIL"))
    public void spreadRandomTick(BlockState state, ServerWorld world, BlockPos pos, Random random, CallbackInfo callback) {
        if (Tags.CROP.contains(state.getBlock())) {
            if (Tags.SPREADABLE.contains(state.getBlock())) {
                if (state.get(getAgeProperty()) >= getMaxAge()) {
                    int spreadRadius = Demeter.config.getSpreadRadius();
                    for (int x = -spreadRadius; x <= spreadRadius; x++) {
                        for (int z = -spreadRadius; z <= spreadRadius; z++) {
                            BlockPos spreadPos = pos.add(x, 0, z);
                            if (random.nextFloat() < Demeter.config.getSpreadProbability()) {
                                BlockState spreadState = state;
                                spreadState = spreadState.with(getAgeProperty(), 0);
                                for (CropProperty property : Demeter.CROP_PROPERTIES.getProperties()) {
                                    spreadState = property.mutate(spreadState, random);
                                }
                                if (world.isAir(spreadPos) && canPlaceAt(spreadState, world, spreadPos)) {
                                    world.setBlockState(spreadPos, spreadState);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Inject(method = "applyGrowth", at = @At(value = "INVOKE", target="Lnet/minecraft/world/World;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;I)Z", shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILHARD)
    public void applyGrowth(World world, BlockPos pos, BlockState state, CallbackInfo callback, int i) {
        if (Tags.CROP.contains(this)) {
            world.setBlockState(pos, state.with(getAgeProperty(), i), 2);
        }
    }
}