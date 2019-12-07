package com.wtoll.demeter.mixin;

import com.wtoll.demeter.api.property.Mutatable;
import com.wtoll.demeter.property.CropProperties;
import com.wtoll.demeter.Demeter;
import com.wtoll.demeter.api.mixin.ICropBlockMixin;
import net.minecraft.block.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.WaterFluid;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.*;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.BlockView;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

@Mixin(CropBlock.class)
public abstract class CropBlockMixin extends PlantBlock implements ICropBlockMixin {
    protected CropBlockMixin(Settings settings) {
        super(settings);
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void initialize(Block.Settings settings, CallbackInfo callback) {

    }

    @Inject(method = "appendProperties", at = @At("HEAD"))
    public void appendProperties(StateManager.Builder<Block, BlockState> builder, CallbackInfo callback) {
        Demeter.CROP_PROPERTIES.forEach(builder::add);
    }

    /**
     * Override the default value for has random ticks to shift the entire logic to standardized ticks
     * @param state
     * @return      Always returns false to prevent unwanted random ticks
     */
    @Override
    public boolean hasRandomTicks(BlockState state) {
        return false;
    }

    /**
     * When the block is added to the world, register it for updates every second
     * @param state
     * @param world
     * @param pos
     * @param oldState
     * @param moved
     */
    @Deprecated
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean moved) {
        world.getBlockTickScheduler().schedule(pos, world.getBlockState(pos).getBlock(), 20);
    }

    @Override
    @SuppressWarnings("deprecation")
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (this.getAge(state) == this.getMaxAge()) {
            dropStacks(state, world, pos);
            world.setBlockState(pos, state.with(this.getAgeProperty(), this.harvestAge()));
            return ActionResult.SUCCESS;
        }

        return super.onUse(state, world, pos, player, hand, hit);
    }

    @SuppressWarnings("deprecation")
    public List<ItemStack> getDroppedStacks(BlockState state, net.minecraft.loot.context.LootContext.Builder builder) {
        return addStateToStacks(state, super.getDroppedStacks(state, builder));
    }

    private List<ItemStack> addStateToStacks(BlockState state, List<ItemStack> stacks) {
        stacks.forEach((stack) -> {
            Item item = stack.getItem();
            if (item instanceof BlockItem && ((BlockItem) item).getBlock().equals(this)) {
                CompoundTag tag = stack.getOrCreateSubTag("BlockStateTag");
                Demeter.CROP_PROPERTIES.forEach((property) -> {
                        tag.putString(property.getName(), state.get((IntProperty) property).toString());
                });
                stack.putSubTag("BlockStateTag", tag);
            }
            stack.setCount((int) (stack.getCount() * Math.ceil((state.get(CropProperties.YIELD) + 1) / 2.5f)));
        });
        return stacks;
    }

    public BlockState withAge(BlockState state, int i) {
        return i > this.getMaxAge() ? state.with(this.getAgeProperty(), this.getMaxAge()) : state.with(this.getAgeProperty(), i);
    }

    /**
     * Gets called on either a random tick or a scheduled tick for standardized logic
     * @param state
     * @param world
     * @param pos
     * @param random
     * @param callback
     */
    @SuppressWarnings("deprecation")
    @Inject(method = "scheduledTick", at = @At("HEAD"), cancellable = true)
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random, CallbackInfo callback) {

        int maxChances = 20;
        int chances = 1;

        chances *= getLightCombined(pos, world);
        maxChances *= 15;

        chances *= world.isRaining() ? 1 : 2;
        maxChances *= 2;

        chances *= getAvailableMoisture(world.getBlockState(pos).getBlock(), world, pos);
        maxChances *= 9;

        chances *= Math.pow(2, state.get(CropProperties.GROWTH) + 1);
        maxChances *= Math.pow(2, 10);

        BlockState floor_state = world.getBlockState(pos.down());
        if (floor_state.getProperties().contains(com.wtoll.demeter.Properties.FERTILIZED)) {
            chances *= floor_state.get(com.wtoll.demeter.Properties.FERTILIZED) ? 1 : 2;
        }

        maxChances *= 2;

        // Multiply by mixed in factors
        chances = additionalChances(chances, state, world, pos, random);
        maxChances = additionalMaxChances(maxChances, state, world, pos, random);

        // Avoid a divide by zero error
        if (random.nextInt(maxChances) < chances) {
            update(state, world, pos, random);
        }

        world.getBlockTickScheduler().schedule(pos, world.getBlockState(pos).getBlock(), 0);
        callback.cancel();
    }

    public int getLightCombined(BlockPos pos, World world) {
        int ambient = 0;
        if (world.dimension.hasSkyLight()) {
            ambient = world.getLightLevel(LightType.SKY, pos) - world.getAmbientDarkness();
            float f = world.getSkyAngleRadians(1.0F);
            if (ambient > 0) {
                float g = f < 3.1415927F ? 0.0F : 6.2831855F;
                f += (g - f) * 0.2F;
                ambient = Math.round((float)ambient * MathHelper.cos(f));
            }

            ambient = MathHelper.clamp(ambient, 0, 15);
        }
        int block = world.getLightLevel(LightType.BLOCK, pos);
        return ambient > block ? ambient : block;
    }


    public int additionalChances(int currentChances, BlockState state, ServerWorld world, BlockPos pos, Random random) {
        return currentChances;
    }

    public int additionalMaxChances(int currentMaxChances, BlockState state, ServerWorld world, BlockPos pos, Random random) {
        return currentMaxChances;
    }


    public void update(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (state.get(getAgeProperty()) >= getMaxAge()) {
            BlockPos spreadPosition;
            List<BlockPos> spreadPositions = validSpreadPositions(world, pos);
            if (spreadPositions.size() > 0) {
                spreadPosition = spreadPositions.get(random.nextInt(spreadPositions.size()));
                world.setBlockState(spreadPosition, constructSpreadState(withAge(state, 0), world, pos));
            }
        } else {
            world.setBlockState(pos, addAge(state));
        }
    }

    private BlockState constructSpreadState(BlockState state, World world, BlockPos pos) {
        BlockState spreadState = state;
        Iterator<Property> i = Demeter.CROP_PROPERTIES.iterator();
        while(i.hasNext()) {
            Property property = i.next();
            if (property instanceof Mutatable) {
                spreadState = spreadState.with(property, ((Mutatable) property).mutateValue(state, world, pos));
            }
        }
        return spreadState;
    }

    public List<BlockPos> validSpreadPositions(World world, BlockPos pos) {
        List<BlockPos> airBlocks = new ArrayList<BlockPos>();
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                BlockPos testPosition = pos.add(x, 0, z);
                if (world.getBlockState(pos.add(x, 0, z)).isAir() && canPlaceAt(world.getBlockState(pos), world, testPosition)) {
                    airBlocks.add(testPosition);
                }
            }
        }
        return airBlocks;
    }

    public BlockState addAge(BlockState state) {
        return withAge(state, state.get(getAgeProperty()) + 1);
    }

    @Inject(method = "applyGrowth", at = @At("HEAD"), cancellable = true)
    public void applyGrowth(World world, BlockPos pos, BlockState state, CallbackInfo callback) {
        int i = this.getAge(state) + this.getGrowthAmount(world);
        int j = this.getMaxAge();
        if (i > j) {
            i = j;
        }
        world.setBlockState(pos, this.withAge(state, i), 2);
        callback.cancel();
    }

    @Overwrite
    public static float getAvailableMoisture(Block block, BlockView world, BlockPos pos) {
        BlockPos groundPos = pos.down(1);
        int moisture = 0;
        // Iterate through the 9 blocks under the crop
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                BlockState state = world.getBlockState(groundPos.add(x, 0, z));
                if (state.getBlock() instanceof FarmlandBlock) {
                    if (state.get(Properties.MOISTURE) >= 7) {
                        moisture++;
                    }
                }
                if (state.getBlock() == Blocks.WATER) {
                    moisture++;
                }
            }
        }
        return moisture;
    }

    @Shadow
    protected int getGrowthAmount(World world) {
        return 0;
    }


    @Shadow
    public int getAge(BlockState state) { return 0; }

    @Shadow
    public int getMaxAge() { return 0; }

    @Shadow
    public IntProperty getAgeProperty() { return null; }
}
