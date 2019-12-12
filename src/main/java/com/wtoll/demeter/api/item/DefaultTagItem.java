package com.wtoll.demeter.api.item;

import com.wtoll.demeter.Demeter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.state.property.Property;

import java.util.Iterator;

public interface DefaultTagItem {
    default CompoundTag getDefaultTag(CompoundTag tag) {
        CompoundTag blockStateTag = tag.getCompound("BlockStateTag");
        if (blockStateTag == null) {
            blockStateTag = new CompoundTag();
        }
        Iterator<Property> i = Demeter.CROP_PROPERTIES.iterator();
        while (i.hasNext()) {
            Property property = i.next();
            blockStateTag.putString(property.getName(), property.getValues().toArray()[0].toString());
        }
        tag.put("BlockStateTag", blockStateTag);
        return tag;
    }
}
