package com.wtoll.demeter;

import com.wtoll.demeter.block.Blocks;
import com.wtoll.demeter.container.Containers;
import com.wtoll.demeter.item.Items;
import com.wtoll.demeter.property.CropProperties;
import net.fabricmc.api.ModInitializer;
import net.minecraft.state.property.Property;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.SimpleRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.Supplier;

public class Demeter implements ModInitializer {

    public static final String MODID = "demeter";
    public static final Logger LOGGER = LogManager.getLogger("Demeter");

    public static final Registry<Property> CROP_PROPERTIES = createRegistry("crop_properties", () -> {
        return CropProperties.YIELD;
    });

    public static CropProperties props = new CropProperties();

    @Override
    public void onInitialize() {
        Containers.initialize();
        Blocks.initialize();
        Items.initialize();
    }

    @SuppressWarnings("unchecked")
    private static <T> Registry<T> createRegistry(String id, Supplier<T> supplier) {
        Identifier identifier = new Identifier(id);
        return Registry.REGISTRIES.add(identifier, new SimpleRegistry());
    }
}
