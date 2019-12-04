package com.wtoll.demeter;

import net.minecraft.state.property.BooleanProperty;

public class Properties {
    public static final BooleanProperty FERTILIZED;

    static {
        FERTILIZED = BooleanProperty.of("fertilized");
    }
}
