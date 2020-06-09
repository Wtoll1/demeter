package com.wtoll.demeter.mixin;

import com.wtoll.demeter.Demeter;
import net.minecraft.block.CropBlock;
import net.minecraft.state.State;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Property;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author <Wtoll> Will Toll on 2020-05-29
 * @project demeter
 */
@Mixin(StateManager.Builder.class)
public class StateManagerBuilderMixin<O, S extends State<O, S>> {
    @Shadow
    public StateManager.Builder<O, S> add(Property... properties) {
        return (StateManager.Builder<O, S>) (Object) this;
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    public void constructor(O owner, CallbackInfo callback) {
        if (owner instanceof CropBlock) {
            Demeter.CROP_PROPERTIES.forEach((property) -> add(property.blockProperty()));
        }
    }
}
