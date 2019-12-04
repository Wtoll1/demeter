package com.wtoll.demeter.api.property;


import net.minecraft.block.BlockState;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface Mutatable<K extends Comparable<K>> extends Property<K> {
    public K mutateValue(BlockState state, World world, BlockPos pos);
}
