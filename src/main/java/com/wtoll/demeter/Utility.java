package com.wtoll.demeter;

import net.minecraft.util.Identifier;

public class Utility {
    public static Identifier id(String s) {
        return new Identifier(Demeter.MODID, s);
    }
}
