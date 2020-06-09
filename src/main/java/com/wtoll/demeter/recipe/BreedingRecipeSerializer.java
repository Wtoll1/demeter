package com.wtoll.demeter.recipe;

import com.google.gson.JsonObject;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

/**
 * @author <Wtoll> Will Toll on 2020-05-28
 * @project demeter
 */
public class BreedingRecipeSerializer implements RecipeSerializer<BreedingRecipe> {
    private final RecipeFactory recipeFactory;

    public BreedingRecipeSerializer(RecipeFactory recipeFactory) {
        this.recipeFactory = recipeFactory;
    }

    @Override
    public BreedingRecipe read(Identifier id, JsonObject json) {
        Identifier result = new Identifier(JsonHelper.getString(json, "result"));
        Identifier parent1 = new Identifier(JsonHelper.getString(json, "parent1"));
        Identifier parent2 = new Identifier(JsonHelper.getString(json, "parent2"));
        return this.recipeFactory.create(id, result, parent1, parent2);
    }

    @Override
    public BreedingRecipe read(Identifier id, PacketByteBuf buf) {
        Identifier result = new Identifier(buf.readString());
        Identifier parent1 = new Identifier(buf.readString());
        Identifier parent2 = new Identifier(buf.readString());
        return this.recipeFactory.create(id, result, parent1, parent2);
    }

    @Override
    public void write(PacketByteBuf buf, BreedingRecipe recipe) {
        buf.writeString(recipe.result().toString());
        buf.writeString(recipe.parent1().toString());
        buf.writeString(recipe.parent2().toString());
    }

    interface RecipeFactory {
        BreedingRecipe create(Identifier id, Identifier result, Identifier parent1, Identifier parent2);
    }
}
