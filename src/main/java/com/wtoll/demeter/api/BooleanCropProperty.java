package com.wtoll.demeter.api;

import com.wtoll.demeter.Demeter;
import net.minecraft.block.BlockState;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.Identifier;

import java.util.Random;

/**
 * @author <Wtoll> Will Toll on 2020-05-28
 * @project Demeter
 */
public class BooleanCropProperty extends CropProperty<Boolean> {
    public BooleanCropProperty(Identifier id) {
        super(id);
        this.blockProperty = BooleanProperty.of(getName());
    }

    public BlockState mutate(BlockState state, Random random) {
        if (random.nextFloat() < Demeter.config.getMutationProbability()) {
            if (!(boolean)state.get(this.blockProperty)) {
                return state.with(this.blockProperty, true);
            }
        }
        return state;
    }

    public BlockState breed(BlockState state, BlockState parent1, BlockState parent2, Random random) {
        return state.with(this.blockProperty, random.nextBoolean() ? (boolean) parent1.get(this.blockProperty) : (boolean) parent2.get(this.blockProperty));
    }
}
