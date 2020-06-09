package com.wtoll.demeter.recipe;

import com.wtoll.demeter.Demeter;
import net.minecraft.inventory.Inventory;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import java.util.Optional;

/**
 * @author <Wtoll> Will Toll on 2020-05-28
 * @project demeter
 */
public class Recipes {
    public static final RecipeSerializer<BreedingRecipe> BREEDING_SERIALIZER = Registry.register(Registry.RECIPE_SERIALIZER, Demeter.id("breeding"), new BreedingRecipeSerializer(BreedingRecipe::new));

    public static final RecipeType<BreedingRecipe> BREEDING_TYPE = Registry.register(Registry.RECIPE_TYPE, Demeter.id("breeding"), new RecipeType<BreedingRecipe>() {
        public String toString() {
            return "breeding";
        }
    });

    public static void init() {

    }
}
