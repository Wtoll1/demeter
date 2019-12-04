package com.wtoll.demeter.property;

import com.wtoll.demeter.Demeter;
import com.wtoll.demeter.Utility;
import com.wtoll.demeter.api.property.MutatableIntProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Property;
import net.minecraft.util.registry.Registry;

public class CropProperties {
    public static final MutatableIntProperty YIELD;
    public static final MutatableIntProperty GROWTH;

    // Empty constructor to just get the static fields defined before initialization
    public CropProperties() { }

    static {
        YIELD = (MutatableIntProperty) register(new MutatableIntProperty("yield", 0, 9, 0.05f));
        GROWTH = (MutatableIntProperty) register(new MutatableIntProperty("growth", 0, 9, 0.05f));
    }

    public static Property register(Property p) {
        return Registry.register(Demeter.CROP_PROPERTIES, Utility.id(p.getName()), p);
    }
}
