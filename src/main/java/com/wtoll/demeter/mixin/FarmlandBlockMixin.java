package com.wtoll.demeter.mixin;

import com.wtoll.demeter.Properties;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FarmlandBlock;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Property;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FarmlandBlock.class)
public abstract class FarmlandBlockMixin extends Block {
    private static final BooleanProperty FERTILIZED;

    public FarmlandBlockMixin(Settings settings) {
        super(settings);
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void initialize(Block.Settings settings, CallbackInfo callback) {
        this.setDefaultState(this.getDefaultState().with(FERTILIZED, false));
    }

    @Inject(method = "appendProperties", at = @At("HEAD"))
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder, CallbackInfo callback) {
        builder.add(FERTILIZED);
    }

    public boolean isFertilized(BlockState state) {
        return state.get(FERTILIZED);
    }


    static {
        FERTILIZED = Properties.FERTILIZED;
    }
}
