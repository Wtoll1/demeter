package com.wtoll.demeter.api;

import com.wtoll.demeter.Demeter;
import net.minecraft.block.BlockState;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.Identifier;

import java.util.Random;

/**
 * @author <Wtoll> Will Toll on 2020-05-28
 * @project Demeter
 */
public class IntCropProperty extends CropProperty<Integer> {
    private final int min;
    private final int max;

    public IntCropProperty(Identifier id, int min, int max) {
        super(id);
        this.min = min;
        this.max = max;
        this.blockProperty = IntProperty.of(getName(), min, max);
    }

    public int max() {
        return max;
    }

    public int min() {
        return min;
    }

    public BlockState mutate(BlockState state, Random random) {
        if (random.nextFloat() < Demeter.config.getMutationProbability()) {
            if ((int) state.get(this.blockProperty) < this.max) {
                return state.with(this.blockProperty, (int) state.get(this.blockProperty) + 1);
            }
        }
        return state;
    }

    public BlockState breed(BlockState state, BlockState parent1, BlockState parent2, Random random) {
        int one = (int) parent1.get(this.blockProperty);
        int oneComp = 0;
        if (one > 0) {
            oneComp = random.nextInt(one/2);
        }
        int two = (int) parent2.get(this.blockProperty);
        int twoComp = 0;
        if (two > 0) {
            twoComp = random.nextInt(two/2);
        }
        return state.with(this.blockProperty, oneComp + twoComp);
    }
}
