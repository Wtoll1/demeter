package com.wtoll.demeter;

import com.wtoll.demeter.api.CropProperty;
import com.wtoll.demeter.api.IntCropProperty;
import com.wtoll.demeter.config.Config;
import com.wtoll.demeter.loottable.LootTables;
import com.wtoll.demeter.recipe.Recipes;
import com.wtoll.demeter.registry.CropPropertyRegistry;
import com.wtoll.demeter.tag.Tags;
import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Demeter implements ModInitializer {

    public static final String MODID = "demeter";
    public static final Logger LOGGER = LogManager.getLogger("Demeter");

    public static final Config config = Config.load();

    public static final CropPropertyRegistry CROP_PROPERTIES = new CropPropertyRegistry();

    public static final CropProperty YIELD = CROP_PROPERTIES.register(new IntCropProperty(new Identifier("c", "yield"), 0, 9));
    public static final CropProperty GROWTH = CROP_PROPERTIES.register(new IntCropProperty(new Identifier("c", "growth"), 0, 9));

    @Override
    public void onInitialize() {
        Tags.init();
        LootTables.init();
        Recipes.init();
    }

    public static Identifier id(String s) {
        return new Identifier(MODID, s);
    }
}
