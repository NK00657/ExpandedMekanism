package io.github.nk00657.expandedmekanism.dategen

import mekanism.api.datagen.recipe.builder.PressurizedReactionRecipeBuilder
import mekanism.api.recipes.ingredients.creator.IngredientCreatorAccess
import net.minecraft.data.PackOutput
import net.minecraft.data.recipes.FinishedRecipe
import net.minecraft.data.recipes.RecipeProvider
import java.util.function.Consumer

class ModRecipeProvider(
    output: PackOutput
) : RecipeProvider(output){
    override fun buildRecipes(consumer: Consumer<FinishedRecipe?>) {

    }
}