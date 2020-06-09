package com.wtoll.demeter.recipe;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * @author <Wtoll> Will Toll on 2020-05-28
 * @project demeter
 */
public class BreedingRecipe implements Recipe<Inventory> {
    private final Identifier id;

    private final Identifier parent1;
    private final Identifier parent2;
    private final Identifier result;

    public BreedingRecipe(Identifier id, Identifier result, Identifier parent1, Identifier parent2) {
        this.id = id;
        this.result = result;
        this.parent1 = parent1;
        this.parent2 = parent2;
    }

    public Identifier parent1() {
        return parent1;
    }

    public Identifier parent2() {
        return parent2;
    }

    public Identifier result() {
        return result;
    }

    @Override
    public boolean matches(Inventory inv, World world) {
        return true;
    }

    @Override
    public ItemStack craft(Inventory inv) {
        return null;
    }

    @Override
    public boolean fits(int width, int height) {
        return false;
    }

    @Override
    public ItemStack getOutput() {
        return null;
    }

    public Identifier getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Recipes.BREEDING_SERIALIZER;
    }

    @Override
    public RecipeType<?> getType() {
        return Recipes.BREEDING_TYPE;
    }

    public static Optional<Identifier> findRecipe(World world, Identifier parent1, Identifier parent2) {
        for (BreedingRecipe recipe : world.getRecipeManager().getAllMatches(Recipes.BREEDING_TYPE, null, world)) {
            if ((parent1.equals(recipe.parent1()) && parent2.equals(recipe.parent2())) || (parent1.equals(recipe.parent2()) && parent2.equals(recipe.parent1()))) {
                return Optional.of(recipe.result);
            }
        }
        return Optional.empty();
    }

    @Override
    public boolean isIgnoredInRecipeBook() {
        return true;
    }
}
