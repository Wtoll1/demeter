package com.wtoll.demeter.api.property;

import net.minecraft.block.BlockState;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Collections;
import java.util.Random;

public class MutatableIntProperty extends IntProperty implements Mutatable<Integer> {
    private float mutationProb;

    public MutatableIntProperty(String name, int min, int max, float mutationProb) {
        super(name, min, max);
        this.mutationProb = mutationProb;
    }

    @Override
    public Integer mutateValue(BlockState state, World world, BlockPos pos) {
        Random random = new Random();
        int i = state.get(this);
        if (Math.random() <= this.mutationProb) {
            if (i + 1 <= Collections.max(this.getValues())) {
                return i + 1;
            }
        }
        return i;
    }
}
