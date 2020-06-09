package com.wtoll.demeter.api;

import com.wtoll.demeter.tag.Tags;
import net.minecraft.block.BlockState;
import net.minecraft.state.property.Property;
import net.minecraft.util.Identifier;

import java.util.Random;
import java.util.function.Consumer;

/**
 * @author <Wtoll> Will Toll on 2020-05-28
 * @project Demeter
 */
public abstract class CropProperty<T> {
    private final Identifier id;

    protected Property blockProperty;

    public CropProperty(Identifier id) {
        this.id = id;
    }

    public Property blockProperty() {
        return blockProperty;
    }

    public T get(BlockState state) {
        if (Tags.CROP.contains(state.getBlock())) {
            return (T) state.get(blockProperty);
        }
        return null;
    }

    public void forState(Consumer<T> action) {
        blockProperty.getValues().forEach((value) -> action.accept((T) value));
    }

    public String getName() {
        return this.id.toString().replace(":", "_");
    }

    public String getTextPath() {
        return "text." + this.id.toString().replace(":", ".");
    }

    public abstract BlockState mutate(BlockState state, Random random);

    public abstract BlockState breed(BlockState state, BlockState parent1, BlockState parent2, Random random);
}
